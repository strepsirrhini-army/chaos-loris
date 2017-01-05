/*
 * Copyright 2015-2017 the original author or authors.
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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static io.pivotal.strepsirrhini.chaosloris.destroyer.FateEngine.Fate.THUMBS_DOWN;

final class StandardDestroyer implements Destroyer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChaosRepository chaosRepository;

    private final EventRepository eventRepository;

    private final FateEngine fateEngine;

    private final Platform platform;

    private final Long scheduleId;

    StandardDestroyer(ChaosRepository chaosRepository, EventRepository eventRepository, FateEngine fateEngine, Platform platform, Long scheduleId) {
        this.chaosRepository = chaosRepository;
        this.eventRepository = eventRepository;
        this.fateEngine = fateEngine;
        this.platform = platform;
        this.scheduleId = scheduleId;
    }

    @Override
    public void run() {
        Flux
            .fromIterable(this.chaosRepository.findByScheduleId(this.scheduleId))
            .flatMap(chaos -> terminate(chaos, this.eventRepository, this.fateEngine, this.platform, this.logger))
            .subscribe();
    }

    private static Mono<Integer> getInstanceCount(Chaos chaos, Platform platform) {
        return platform.getInstanceCount(chaos.getApplication());
    }

    private static Mono<Integer> terminate(Application application, Integer index, Platform platform) {
        return platform.terminateInstance(application, index)
            .flatMap(null, t -> Mono.empty(), () -> Mono.just(index))
            .next();
    }

    private static Mono<Event> terminate(Chaos chaos, EventRepository eventRepository, FateEngine fateEngine, Platform platform, Logger logger) {
        return getInstanceCount(chaos, platform)
            .then(instanceCount -> Flux.range(0, instanceCount)
                .flatMap(index -> Mono.just(fateEngine.getFate(chaos))
                    .filter(fate -> THUMBS_DOWN == fate)
                    .then(terminate(chaos.getApplication(), index, platform)))
                .collectList()
                .map(terminatedInstances -> new Event(chaos, Instant.now(), terminatedInstances, instanceCount)))
            .then(event -> Mono.just(eventRepository.save(event)))
            .doOnSubscribe(s -> logger.info("Start {}", chaos))
            .doOnSuccess(e -> logger.debug("Finish {}", chaos));
    }

}
