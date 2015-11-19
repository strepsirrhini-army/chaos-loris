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
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.pivotal.strepsirrhini.chaosloris.MatchesPattern.matchesPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScheduleControllerTest extends AbstractControllerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void create() throws Exception {
        assertThat(this.scheduleRepository.count()).isEqualTo(0);

        String content = asJson(MapBuilder.builder()
                .entry("expression", "test-expression")
                .entry("name", "test-name")
                .build());

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/schedules/[\\d]+")));

        assertThat(this.scheduleRepository.count()).isEqualTo(1);
    }

    @Test
    public void createDuplicateName() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("expression", "test-expression")
                .entry("name", "test-name")
                .build());

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullName() throws Exception {
        String content = asJson(MapBuilder.builder()
                .entry("expression", "test-expression")
                .build());

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullExpression() throws Exception {
        String content = asJson(MapBuilder.builder()
                .entry("expression", "test-expression")
                .build());

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void del() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        this.mockMvc.perform(delete("/schedules/{id}", schedule.getId()))
                .andExpect(status().isNoContent());

        assertThat(this.scheduleRepository.exists(schedule.getId())).isFalse();
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        this.mockMvc.perform(delete("/schedules/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        this.mockMvc.perform(get("/schedules").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$._embedded.schedules").value(hasSize(1)))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void read() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        this.mockMvc.perform(get("/schedules/{id}", schedule.getId()).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expression").value("test-expression"))
                .andExpect(jsonPath("$.name").value("test-name"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void readDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/schedules/{id}", Long.MAX_VALUE).accept(HAL_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("expression", "another-test-expression")
                .entry("name", "another-test-name")
                .build());

        this.mockMvc.perform(patch("/schedules/{id}", schedule.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());

        assertThat(schedule.getExpression()).isEqualTo("another-test-expression");
        assertThat(schedule.getName()).isEqualTo("another-test-name");
    }

    @Test
    public void updateExpressionOnly() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("expression", "another-test-expression")
                .build());

        this.mockMvc.perform(patch("/schedules/{id}", schedule.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());

        assertThat(schedule.getExpression()).isEqualTo("another-test-expression");
    }

    @Test
    public void updateNameOnly() throws Exception {
        Schedule schedule = new Schedule("test-expression", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
                .entry("name", "another-test-name")
                .build());

        this.mockMvc.perform(patch("/schedules/{id}", schedule.getId()).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNoContent());

        assertThat(schedule.getName()).isEqualTo("another-test-name");
    }

    @Test
    public void updateDoesNotExist() throws Exception {
        String content = asJson(MapBuilder.builder()
                .entry("expression", "another-test-expression")
                .entry("name", "another-test-name")
                .build());

        this.mockMvc.perform(patch("/schedules/{id}", Long.MAX_VALUE).contentType(APPLICATION_JSON).content(content))
                .andExpect(status().isNotFound());
    }

}
