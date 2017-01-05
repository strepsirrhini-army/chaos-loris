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

import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleCreatedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleDeletedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleUpdatedEvent;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private TestApplicationEventListener applicationEventListener;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScheduleController scheduleController;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ScheduleRepository scheduleRepository;

    @Test
    public void create() throws Exception {
        this.applicationEventListener.clear();

        when(this.scheduleRepository.saveAndFlush(new Schedule("test-expression", "test-name")))
            .then(invocation -> {
                Schedule schedule = invocation.getArgumentAt(0, Schedule.class);
                schedule.setId(1L);
                return schedule;
            });

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("expression", "test-expression")
                .entry("name", "test-name")
                .build())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/schedules/1L"));

        Schedule schedule = new Schedule("test-expression", "test-name");
        schedule.setId(1L);

        this.applicationEventListener.getEvents()
            .as(StepVerifier::create)
            .expectNext(new ScheduleCreatedEvent(this.scheduleController, schedule))
            .expectComplete()
            .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createDuplicateName() throws Exception {
        when(this.scheduleRepository.saveAndFlush(new Schedule("test-expression", "test-name")))
            .thenThrow(DataIntegrityViolationException.class);

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("expression", "test-expression")
                .entry("name", "test-name")
                .build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullExpression() throws Exception {
        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("expression", "test-expression"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullName() throws Exception {
        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("expression", "test-expression"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void del() throws Exception {
        this.applicationEventListener.clear();

        this.mockMvc.perform(delete("/schedules/{id}", 1L))
            .andExpect(status().isNoContent());

        verify(this.scheduleRepository).delete(1L);

        this.applicationEventListener.getEvents()
            .as(StepVerifier::create)
            .expectNext(new ScheduleDeletedEvent(this.scheduleController, 1L))
            .expectComplete()
            .verify();
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        doThrow(EmptyResultDataAccessException.class)
            .when(this.scheduleRepository).delete(1L);

        this.mockMvc.perform(delete("/schedules/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        schedule.setId(1L);

        when(this.scheduleRepository.findAll(new PageRequest(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(schedule)));

        this.mockMvc.perform(get("/schedules").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$._embedded.schedules").value(hasSize(1)))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void read() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        schedule.setId(1L);

        when(this.scheduleRepository.getOne(schedule.getId()))
            .thenReturn(schedule);

        this.mockMvc.perform(get("/schedules/{id}", schedule.getId()).accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.expression").value("test-expression"))
            .andExpect(jsonPath("$.name").value("test-name"))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readDoesNotExist() throws Exception {
        when(this.scheduleRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);

        this.mockMvc.perform(get("/schedules/{id}", 1L).accept(HAL_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void update() throws Exception {
        this.applicationEventListener.clear();

        Schedule schedule1 = new Schedule("test-expression", "test-name");
        schedule1.setId(1L);

        when(this.scheduleRepository.getOne(schedule1.getId()))
            .thenReturn(schedule1);

        this.mockMvc.perform(patch("/schedules/{id}", schedule1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("expression", "another-test-expression")
                .entry("name", "another-test-name")
                .build())))
            .andExpect(status().isNoContent());

        Schedule schedule2 = new Schedule("another-test-expression", "another-test-name");
        schedule2.setId(schedule1.getId());

        verify(this.scheduleRepository).save(schedule2);

        this.applicationEventListener.getEvents()
            .as(StepVerifier::create)
            .expectNext(new ScheduleUpdatedEvent(this.scheduleController, schedule2))
            .expectComplete()
            .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateDoesNotExist() throws Exception {
        when(this.scheduleRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);

        this.mockMvc.perform(patch("/schedules/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("expression", "another-test-expression")
                .entry("name", "another-test-name")
                .build())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateExpressionOnly() throws Exception {
        this.applicationEventListener.clear();

        Schedule schedule1 = new Schedule("test-expression", "test-name");
        schedule1.setId(1L);

        when(this.scheduleRepository.getOne(schedule1.getId()))
            .thenReturn(schedule1);

        this.mockMvc.perform(patch("/schedules/{id}", schedule1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("expression", "another-test-expression"))))
            .andExpect(status().isNoContent());

        Schedule schedule2 = new Schedule("another-test-expression", "test-name");
        schedule2.setId(schedule1.getId());

        verify(this.scheduleRepository).save(schedule2);

        this.applicationEventListener.getEvents()
            .as(StepVerifier::create)
            .expectNext(new ScheduleUpdatedEvent(this.scheduleController, schedule2))
            .expectComplete()
            .verify();
    }

    @Test
    public void updateNameOnly() throws Exception {
        this.applicationEventListener.clear();

        Schedule schedule1 = new Schedule("test-expression", "test-name");
        schedule1.setId(1L);

        when(this.scheduleRepository.getOne(schedule1.getId()))
            .thenReturn(schedule1);

        this.mockMvc.perform(patch("/schedules/{id}", schedule1.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("name", "another-test-name"))))
            .andExpect(status().isNoContent());

        Schedule schedule2 = new Schedule("test-expression", "another-test-name");
        schedule2.setId(schedule1.getId());

        verify(this.scheduleRepository).save(schedule2);

        this.applicationEventListener.getEvents()
            .as(StepVerifier::create)
            .expectNext(new ScheduleUpdatedEvent(this.scheduleController, schedule2))
            .expectComplete()
            .verify();
    }

    @EnableSpringDataWebSupport
    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        ScheduleResourceAssembler scheduleResourceAssembler(ChaosRepository chaosRepository) {
            return new ScheduleResourceAssembler(chaosRepository);
        }

    }

    @TestComponent
    static class TestApplicationEventListener {

        private final List<ApplicationEvent> event = new ArrayList<>();

        @EventListener
        void listen(ScheduleCreatedEvent event) {
            this.event.add(event);
        }

        @EventListener
        void listen(ScheduleDeletedEvent event) {
            this.event.add(event);
        }

        @EventListener
        void listen(ScheduleUpdatedEvent event) {
            this.event.add(event);
        }

        private void clear() {
            this.event.clear();
        }

        private Flux<? super ApplicationEvent> getEvents() {
            return Flux.fromIterable(this.event);
        }

    }

}
