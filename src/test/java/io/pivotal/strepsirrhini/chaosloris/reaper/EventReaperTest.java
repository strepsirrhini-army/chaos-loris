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

import io.pivotal.strepsirrhini.chaosloris.AbstractIntegrationTest;
import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

public class EventReaperTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    private EventReaper eventReaper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void doReap() {
        Schedule schedule = new Schedule("test-schedule", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Instant now = Instant.now();

        Event event1 = new Event(chaos, now.minus(3, DAYS), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event1);

        Event event2 = new Event(chaos, now, Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event2);

        Event event3 = new Event(chaos, now.plus(3, DAYS), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event3);

        this.eventReaper.doReap().get();

        List<Event> events = this.eventRepository.findAll();
        assertThat(events).containsExactly(event2, event3);
    }

    @Before
    public void setUp() throws Exception {
        this.eventReaper = new EventReaper(this.eventRepository, Period.parse("P1D"));
    }

}
