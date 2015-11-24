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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.fn.tuple.Tuple;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.time.Instant;

import static io.pivotal.strepsirrhini.chaosloris.destroyer.FateEngine.Fate.THUMBS_DOWN;

@Data
final class StandardDestroyer implements Destroyer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Long scheduleId;

    private final ChaosRepository chaosRepository;

    private final EventRepository eventRepository;

    private final FateEngine fateEngine;

    private final Platform platform;

    @Override
    public void run() {
        Streams.from(this.chaosRepository.findByScheduleId(this.scheduleId))
                .flatMap(this::terminate)
                .consume();
    }

    private Stream<?> terminate(Chaos chaos) {
        Event.EventBuilder eventBuilder = Event.builder()
                .chaos(chaos)
                .executedAt(Instant.now());

        return this.platform.getInstanceCount(chaos.getApplication())
                .observeStart(s -> this.logger.info("Start {}", chaos))
                .observe(eventBuilder::totalInstanceCount)
                .flatMap(instanceCount -> Streams.range(0, instanceCount))
                .map(instance -> Tuple.of(instance, this.fateEngine.getFate(chaos)))
                .filter(tuple -> THUMBS_DOWN == tuple.getT2())
                .flatMap(tuple -> terminate(chaos.getApplication(), tuple.getT1())
                        .observeComplete(v -> eventBuilder.terminatedInstance(tuple.getT1())))
                .observeComplete(v -> {
                    this.eventRepository.save(eventBuilder.build());
                    this.logger.debug("Finish {}", chaos);
                });
    }

    private Stream<?> terminate(Application application, Integer instance) {
        return this.platform.terminateInstance(application, instance);
    }

}
