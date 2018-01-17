/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaosloris.destroyer;

import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleCreatedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleDeletedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.Lifecycle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;


@Component
final class DestructionScheduler implements HealthIndicator, Lifecycle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DestroyerFactory destroyerFactory;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final ScheduleRepository scheduleRepository;

    private final Map<Long, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    @Autowired
    DestructionScheduler(DestroyerFactory destroyerFactory, ScheduleRepository scheduleRepository, TaskScheduler taskScheduler) {
        this.destroyerFactory = destroyerFactory;
        this.scheduleRepository = scheduleRepository;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public Health health() {
        if (this.running.get()) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    @Override
    public void start() {
        Flux
            .fromIterable(this.scheduleRepository.findAll())
            .flatMap(schedule -> start(schedule, this.destroyerFactory, this.taskScheduler, this.scheduled, this.logger))
            .doOnComplete(() -> this.running.set(true))
            .subscribe();
    }

    @Override
    public void stop() {
        Flux
            .fromIterable(this.scheduled.keySet())
            .flatMap(id -> stop(id, this.scheduled, this.logger))
            .doOnComplete(() -> this.running.set(false))
            .subscribe();
    }

    @TransactionalEventListener
    void scheduleCreated(ScheduleCreatedEvent event) {
        start(event.getSchedule(), this.destroyerFactory, this.taskScheduler, this.scheduled, this.logger)
            .subscribe();
    }

    @TransactionalEventListener
    void scheduleDeleted(ScheduleDeletedEvent event) {
        stop(event.getId(), this.scheduled, this.logger)
            .subscribe();
    }

    @TransactionalEventListener
    void scheduleUpdated(ScheduleUpdatedEvent event) {
        Schedule schedule = event.getSchedule();
        Flux
            .concat(
                stop(schedule.getId(), this.scheduled, this.logger),
                start(schedule, this.destroyerFactory, this.taskScheduler, this.scheduled, this.logger)
            )
            .subscribe();
    }

    private static Mono<ScheduledFuture<?>> putSchedule(Map<Long, ScheduledFuture<?>> scheduled, Long id, ScheduledFuture<?> future) {
        return Optional
            .ofNullable(scheduled.put(id, future))
            .map((Function<ScheduledFuture<?>, Mono<ScheduledFuture<?>>>) Mono::just)
            .orElse(Mono.empty());
    }

    private static Mono<ScheduledFuture<?>> removeSchedule(Map<Long, ScheduledFuture<?>> scheduled, Long id) {
        return Optional
            .ofNullable(scheduled.remove(id))
            .map((Function<ScheduledFuture<?>, Mono<ScheduledFuture<?>>>) Mono::just)
            .orElse(Mono.empty());
    }

    private static Mono<ScheduledFuture<?>> schedule(TaskScheduler taskScheduler, Destroyer destroyer, String expression) {
        return Mono.just(taskScheduler.schedule(destroyer, new CronTrigger(expression)));
    }

    private static Mono<Boolean> start(Schedule schedule, DestroyerFactory destroyerFactory, TaskScheduler taskScheduler, Map<Long, ScheduledFuture<?>> scheduled, Logger logger) {
        return Mono
            .just(schedule)
            .map(s -> destroyerFactory.create(s.getId()))
            .then(destroyer -> schedule(taskScheduler, destroyer, schedule.getExpression()))
            .then(future -> putSchedule(scheduled, schedule.getId(), future))
            .then(previous -> Mono.just(previous.cancel(false)))
            .doOnSubscribe(s -> logger.info("Start {}", schedule));
    }

    private static Mono<Boolean> stop(Long id, Map<Long, ScheduledFuture<?>> scheduled, Logger logger) {
        return Mono
            .just(id)
            .then(i -> removeSchedule(scheduled, i))
            .then(previous -> Mono.just(previous.cancel(false)))
            .doOnSuccess(s -> logger.debug("Stop Schedule(id={})", id));
    }

}

