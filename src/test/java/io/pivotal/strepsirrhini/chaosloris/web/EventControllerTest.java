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
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void del() throws Exception {
        this.mockMvc.perform(delete("/events/{id}", 1L))
            .andExpect(status().isNoContent());

        verify(this.eventRepository).delete(1L);
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        doThrow(EmptyResultDataAccessException.class)
            .when(this.eventRepository).delete(1L);

        this.mockMvc.perform(delete("/events/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(-1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-2L);

        Chaos chaos = new Chaos(application, 0.2, schedule);
        chaos.setId(-3L);

        Event event = new Event(chaos, Instant.EPOCH, Collections.emptyList(), Integer.MIN_VALUE);
        event.setId(-4L);

        when(this.eventRepository.findAll(new PageRequest(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(event)));

        this.mockMvc.perform(get("/events").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$._embedded.events").value(hasSize(1)))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void read() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(-1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-2L);

        Chaos chaos = new Chaos(application, 0.2, schedule);
        chaos.setId(-3L);

        Event event = new Event(chaos, Instant.EPOCH, Arrays.asList(0, 1), Integer.MIN_VALUE);
        event.setId(-4L);

        when(this.eventRepository.getOne(schedule.getId()))
            .thenReturn(event);

        this.mockMvc.perform(get("/events/{id}", schedule.getId()).accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.executedAt").value(Instant.EPOCH.toString()))
            .andExpect(jsonPath("$.terminatedInstances").value(contains(0, 1)))
            .andExpect(jsonPath("$.totalInstanceCount").value(Integer.MIN_VALUE))
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.chaos").exists());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readDoesNotExist() throws Exception {
        when(this.eventRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);
    }

    @EnableSpringDataWebSupport
    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        EventResourceAssembler eventResourceAssembler() {
            return new EventResourceAssembler();
        }

    }

}
