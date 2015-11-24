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

package io.pivotal.strepsirrhini.chaosloris.reaper;

import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.time.Instant;
import java.time.Period;

@Component
class EventReaper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventRepository eventRepository;

    private final Period period;

    @Autowired
    EventReaper(EventRepository eventRepository, @Value("${loris.reaper.history}") Period period) {
        this.eventRepository = eventRepository;
        this.period = period;
    }

    @Scheduled(cron = "${loris.reaper.schedule}")
    public final void reap() {
        doReap()
                .consume();
    }

    final Stream<?> doReap() {
        Instant limit = Instant.now().minus(this.period);

        return Streams.from(this.eventRepository.findByExecutedAtBefore(limit))
                .observeStart(s -> this.logger.info("Reap Events before {}", limit.toString()))
                .flatMap(event -> Streams.just(event)
                        .observeStart(s -> this.logger.info("Reap {}", event))
                        .observe(this.eventRepository::delete)
                        .observeComplete(v -> this.logger.debug("Reaped {}", event)))
                .count()
                .observe(count -> this.logger.debug("Reaped {} Events", count));
    }

}
