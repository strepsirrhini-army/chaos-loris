/*
 * Copyright 2015 the original author or authors.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
final class DestructionScheduler implements HealthIndicator, Lifecycle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Map<Long, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

    private final DestroyerFactory destroyerFactory;

    private final ScheduleRepository scheduleRepository;

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

    @TransactionalEventListener
    public void scheduleCreated(ScheduleCreatedEvent event) {
        start(event.getSchedule());
    }

    @TransactionalEventListener
    public void scheduleDeleted(ScheduleDeletedEvent event) {
        stop(event.getId());
    }

    @TransactionalEventListener
    public void scheduleUpdated(ScheduleUpdatedEvent event) {
        Schedule schedule = event.getSchedule();
        stop(schedule.getId());
        start(schedule);
    }

    @Override
    public void start() {
        this.scheduleRepository.findAll().stream()
                .forEach(this::start);
        this.running.set(true);
    }

    @Override
    public void stop() {
        this.scheduled.keySet().stream()
                .forEach(this::stop);
        this.running.set(false);
    }

    private void start(Schedule schedule) {
        this.logger.debug("Start: {}", schedule);

        Destroyer destroyer = this.destroyerFactory.create(schedule.getId());
        ScheduledFuture<?> future = this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()));

        ScheduledFuture<?> previous = this.scheduled.put(schedule.getId(), future);
        if (previous != null) {
            previous.cancel(false);
        }
    }

    private void stop(Long id) {
        this.logger.debug("Stop: Schedule(id={})", id);

        ScheduledFuture<?> future = this.scheduled.remove(id);
        if (future != null) {
            future.cancel(false);
        }
    }

}

