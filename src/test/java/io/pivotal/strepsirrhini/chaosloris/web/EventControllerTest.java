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

package io.pivotal.strepsirrhini.chaosloris.web;

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventControllerTest extends AbstractControllerTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void del() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Event event = new Event(chaos, Instant.EPOCH, Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event);

        this.mockMvc.perform(delete("/events/{id}", event.getId()))
                .andExpect(status().isNoContent());

        assertThat(this.eventRepository.exists(event.getId())).isFalse();
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        this.mockMvc.perform(delete("/events/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Event event = new Event(chaos, Instant.EPOCH, Collections.emptyList(), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event);

        this.mockMvc.perform(get("/events").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$._embedded.events").value(hasSize(1)))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void read() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Event event = new Event(chaos, Instant.EPOCH, Arrays.asList(0, 1), Integer.MIN_VALUE);
        this.eventRepository.saveAndFlush(event);

        this.mockMvc.perform(get("/events/{id}", event.getId()).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executedAt").value(Instant.EPOCH.toString()))
                .andExpect(jsonPath("$.terminatedInstances").value(contains(0, 1)))
                .andExpect(jsonPath("$.totalInstanceCount").value(Integer.MIN_VALUE))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.chaos").exists());
    }

    @Test
    public void readDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/events/{id}", Long.MAX_VALUE).accept(HAL_JSON))
                .andExpect(status().isNotFound());
    }

}
