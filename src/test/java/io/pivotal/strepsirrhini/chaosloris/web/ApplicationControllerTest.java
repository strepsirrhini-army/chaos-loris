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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.MatchesPattern.matchesPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationControllerTest extends AbstractControllerTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void create() throws Exception {
        assertThat(this.applicationRepository.count()).isEqualTo(0);

        String content = asJson(MapBuilder.builder()
            .entry("applicationId", UUID.randomUUID().toString())
            .build());

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", matchesPattern(".*/applications/[\\d]+")));

        assertThat(this.applicationRepository.count()).isEqualTo(1);
    }

    @Test
    public void createDuplicateApplicationId() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        String content = asJson(MapBuilder.builder()
            .entry("applicationId", application.getApplicationId().toString())
            .build());

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullApplicationId() throws Exception {
        String content = asJson(MapBuilder.builder()
            .build());

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON).content(content))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void del() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        this.mockMvc.perform(delete("/applications/{id}", application.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        this.mockMvc.perform(delete("/applications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        this.mockMvc.perform(get("/applications").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$._embedded.applications").exists())
            .andExpect(jsonPath("$._links").exists());
    }

    @Test
    public void read() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        this.mockMvc.perform(get("/applications/{id}", application.getId()).accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicationId").value(application.getApplicationId().toString()))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void readDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/applications/{id}", Long.MAX_VALUE).accept(HAL_JSON))
            .andExpect(status().isNotFound());
    }

}
