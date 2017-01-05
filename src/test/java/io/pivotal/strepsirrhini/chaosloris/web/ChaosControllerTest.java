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

package io.pivotal.strepsirrhini.chaosloris.web;

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.JsonTestUtilities.asJson;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ChaosController.class)
public class ChaosControllerTest {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ApplicationRepository applicationRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ScheduleRepository scheduleRepository;

    @Test
    public void create() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        when(this.chaosRepository.saveAndFlush(new Chaos(application, 0.1, schedule)))
            .then(invocation -> {
                Chaos chaos = invocation.getArgumentAt(0, Chaos.class);
                chaos.setId(3L);
                return chaos;
            });

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 0.1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/chaoses/3"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createDuplicateApplicationAndSchedule() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        when(this.chaosRepository.saveAndFlush(new Chaos(application, 0.1, schedule)))
            .thenThrow(DataIntegrityViolationException.class);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 0.1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullApplication() throws Exception {
        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("probability", 0.1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullProbability() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullSchedule() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 0.1)
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityGreaterThanOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 1.1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityLessThanZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", -0.1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        when(this.chaosRepository.saveAndFlush(new Chaos(application, 1.0, schedule)))
            .then(invocation -> {
                Chaos chaos = invocation.getArgumentAt(0, Chaos.class);
                chaos.setId(1L);
                return chaos;
            });

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 1)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/chaoses/1"));
    }

    @Test
    public void createProbabilityZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        when(this.chaosRepository.saveAndFlush(new Chaos(application, 0.0, schedule)))
            .then(invocation -> {
                Chaos chaos = invocation.getArgumentAt(0, Chaos.class);
                chaos.setId(1L);
                return chaos;
            });

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "http://localhost/applications/1")
                .entry("probability", 0)
                .entry("schedule", "http://localhost/schedules/2")
                .build())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/chaoses/1"));
    }

    @Test
    public void del() throws Exception {
        this.mockMvc.perform(delete("/chaoses/{id}", 1L))
            .andExpect(status().isNoContent());

        verify(this.chaosRepository).delete(1L);
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        doThrow(EmptyResultDataAccessException.class)
            .when(this.chaosRepository).delete(1L);

        this.mockMvc.perform(delete("/chaoses/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        when(this.chaosRepository.findAll(new PageRequest(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(chaos)));

        this.mockMvc.perform(get("/chaoses").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$._embedded.chaoses").value(hasSize(1)))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void read() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        when(this.chaosRepository.getOne(chaos.getId()))
            .thenReturn(chaos);

        this.mockMvc.perform(get("/chaoses/{id}", chaos.getId()).accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.probability").value(0.1))
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.application").exists())
            .andExpect(jsonPath("$._links.schedule").exists());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readDoesNotExist() throws Exception {
        when(this.chaosRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);

        this.mockMvc.perform(get("/chaoses/{id}", 1L).accept(HAL_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void update() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos1 = new Chaos(application, 0.1, schedule);
        chaos1.setId(3L);

        when(this.chaosRepository.getOne(chaos1.getId()))
            .thenReturn(chaos1);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 0.5))))
            .andExpect(status().isNoContent());

        Chaos chaos2 = new Chaos(application, 0.5, schedule);
        chaos2.setId(chaos1.getId());

        verify(this.chaosRepository).save(chaos2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateDoesNotExist() throws Exception {
        when(this.chaosRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);

        this.mockMvc.perform(patch("/chaoses/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 0.5))))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateProbabilityGreaterThanOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        when(this.chaosRepository.getOne(chaos.getId()))
            .thenReturn(chaos);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 1.1))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProbabilityLessThanZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        when(this.chaosRepository.getOne(chaos.getId()))
            .thenReturn(chaos);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", -0.1))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProbabilityOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos1 = new Chaos(application, 0.1, schedule);
        chaos1.setId(3L);

        when(this.chaosRepository.getOne(chaos1.getId()))
            .thenReturn(chaos1);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 1))))
            .andExpect(status().isNoContent());

        Chaos chaos2 = new Chaos(application, 1.0, schedule);
        chaos2.setId(chaos1.getId());

        verify(this.chaosRepository).save(chaos2);
    }

    @Test
    public void updateProbabilityZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos1 = new Chaos(application, 0.1, schedule);
        chaos1.setId(3L);

        when(this.chaosRepository.getOne(chaos1.getId()))
            .thenReturn(chaos1);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 0))))
            .andExpect(status().isNoContent());

        Chaos chaos2 = new Chaos(application, 0.0, schedule);
        chaos2.setId(chaos1.getId());

        verify(this.chaosRepository).save(chaos2);
    }

    @EnableSpringDataWebSupport
    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        ChaosResourceAssembler chaosResourceAssembler(EventRepository eventRepository) {
            return new ChaosResourceAssembler(eventRepository);
        }

    }

}
