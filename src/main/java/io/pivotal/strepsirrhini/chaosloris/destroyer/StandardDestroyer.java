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

import io.pivotal.strepsirrhini.chaosloris.ErrorConsumer;
import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.stream.IntStream;

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
        this.chaosRepository.findByScheduleId(this.scheduleId).stream()
                .forEach(this::terminate);
    }

    private void terminate(Chaos chaos) {
        this.logger.info("Start: {}", chaos);

        Event.EventBuilder eventBuilder = Event.builder()
                .chaos(chaos)
                .executedAt(Instant.now());

        this.platform.getInstanceCount(chaos.getApplication())
                .consume(instanceCount -> {
                            eventBuilder.totalInstanceCount(instanceCount);
                            terminate(chaos, eventBuilder, instanceCount);
                        }, ErrorConsumer.INSTANCE,
                        v -> this.eventRepository.save(eventBuilder.build()));
    }

    private void terminate(Chaos chaos, Event.EventBuilder eventBuilder, Integer instanceCount) {
        IntStream.range(0, instanceCount).parallel()
                .forEach(instance -> {
                    if (THUMBS_DOWN == this.fateEngine.getFate(chaos)) {
                        eventBuilder.terminatedInstance(instance);
                        terminate(chaos.getApplication(), instance);
                    }
                });
    }

    private void terminate(Application application, Integer instance) {
        this.platform.terminateInstance(application, instance);
    }

}
