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

import io.pivotal.strepsirrhini.chaosloris.MapBuilder;
import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.MatchesPattern.matchesPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChaosControllerTest extends AbstractControllerTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void create() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 0.1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/chaoses/[\\d]+")));

        assertThat(this.chaosRepository.count()).isEqualTo(1);
    }

    @Test
    public void createDuplicateApplicationAndSchedule() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 0.1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullApplication() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("probability", 0.1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullProbability() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullSchedule() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 0.1)
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityLessThanZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", -0.1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 0)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());
    }

    @Test
    public void createProbabilityGreaterThanOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 1.1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProbabilityOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
                .entry("probability", 1)
                .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated());
    }

    @Test
    public void del() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        this.mockMvc.perform(delete("/chaoses/{id}", chaos.getId()))
                .andExpect(status().isNoContent());

        assertThat(this.chaosRepository.exists(chaos.getId())).isFalse();
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        this.mockMvc.perform(delete("/chaoses/{id}", Long.MAX_VALUE))
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

        this.mockMvc.perform(get("/chaoses").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$._embedded.chaoses").value(hasSize(1)))
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

        this.mockMvc.perform(get("/chaoses/{id}", chaos.getId()).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probability").value(0.1))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.application").exists())
                .andExpect(jsonPath("$._links.schedule").exists());
    }

    @Test
    public void readDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/chaoses/{id}", Long.MAX_VALUE).accept(HAL_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("probability", 0.5)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());

        assertThat(chaos.getProbability()).isEqualTo(0.5);
    }

    @Test
    public void updateDoesNotExist() throws Exception {
        String content = asJson(MapBuilder.builder()
                .entry("probability", 0.5)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", Long.MAX_VALUE).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProbabilityLessThanZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("probability", -0.1)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProbabilityZero() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("probability", 0)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateProbabilityGreaterThanOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("probability", 1.1)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProbabilityOne() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String content = asJson(MapBuilder.builder()
                .entry("probability", 1)
                .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());
    }

}
