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

import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@RunWith(SpringRunner.class)
public class EventRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

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

    @Test
    public void findByExecutedAtBefore() throws ExecutionException, InterruptedException {
        Schedule schedule = new Schedule("test-schedule", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Instant now = Instant.now();

        Event event1 = new Event(chaos, now.minus(1, DAYS), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event1);

        Event event2 = new Event(chaos, now, Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event2);

        Event event3 = new Event(chaos, now.plus(1, DAYS), Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event3);

        List<Event> events = this.eventRepository.findByExecutedAtBefore(now.minus(1, HOURS));
        assertThat(events).containsExactly(event1);
    }

}
