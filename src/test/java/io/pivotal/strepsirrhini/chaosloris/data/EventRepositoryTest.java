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

package io.pivotal.strepsirrhini.chaosloris.data;

import io.pivotal.strepsirrhini.chaosloris.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void findByChaos() {
        Schedule schedule = new Schedule("test-schedule", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application1 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application1);

        Chaos chaos1 = new Chaos(application1, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos1);

        Event event1 = new Event(chaos1, Instant.now(), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event1);

        Application application2 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application2);

        Chaos chaos2 = new Chaos(application2, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos2);

        Event event2 = new Event(chaos2, Instant.now(), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event2);

        List<Event> events = this.eventRepository.findByChaos(chaos1);
        assertThat(events).containsExactly(event1);
    }

}
