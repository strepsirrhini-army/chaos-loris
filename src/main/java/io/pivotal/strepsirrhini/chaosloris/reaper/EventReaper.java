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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Period;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Transactional
    public void reap() {
        Instant limit = Instant.now().minus(this.period);
        this.logger.debug("Reaping Events before {}", limit.toString());

        AtomicInteger reapCount = new AtomicInteger();
        this.eventRepository.findByExecutedAtBefore(limit).stream()
                .forEach(event -> {
                    this.eventRepository.delete(event);
                    this.logger.debug("Reaped: {}", event);
                    reapCount.incrementAndGet();
                });

        this.logger.info("Reaped {} Events", reapCount.get());
    }

}
