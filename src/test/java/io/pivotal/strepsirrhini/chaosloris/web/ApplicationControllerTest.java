/*
 * Copyright 2015-2018 the original author or authors.
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
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import org.cloudfoundry.client.CloudFoundryClient;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ApplicationRepository applicationRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create() throws Exception {
        UUID applicationId = UUID.randomUUID();

        when(this.applicationRepository.saveAndFlush(new Application(applicationId)))
            .then(invocation -> {
                Application application = invocation.getArgumentAt(0, Application.class);
                application.setId(1L);
                return application;
            });

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("applicationId", applicationId.toString()))))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/applications/1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createDuplicateApplicationId() throws Exception {
        UUID applicationId = UUID.randomUUID();

        when(this.applicationRepository.saveAndFlush(new Application(applicationId)))
            .thenThrow(DataIntegrityViolationException.class);

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("applicationId", applicationId.toString()))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullApplicationId() throws Exception {
        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON)
            .content(asJson(Collections.emptyMap())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void del() throws Exception {
        this.mockMvc.perform(delete("/applications/{id}", 1L))
            .andExpect(status().isNoContent());

        verify(this.applicationRepository).delete(1L);
    }

    @Test
    public void deleteDoesNotExist() throws Exception {
        doThrow(EmptyResultDataAccessException.class)
            .when(this.applicationRepository).delete(1L);

        this.mockMvc.perform(delete("/applications/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        when(this.applicationRepository.findAll(new PageRequest(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(application)));

        this.mockMvc.perform(get("/applications").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$._embedded.applications").exists())
            .andExpect(jsonPath("$._links").exists());
    }

    @Test
    public void read() throws Exception {
        UUID applicationId = UUID.randomUUID();
        Application application = new Application(applicationId);
        application.setId(1L);

        when(this.applicationRepository.getOne(application.getId()))
            .thenReturn(application);

        this.mockMvc.perform(get("/applications/{id}", application.getId()).accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicationId").value(applicationId.toString()))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readDoesNotExist() throws Exception {
        when(this.applicationRepository.getOne(1L))
            .thenThrow(EntityNotFoundException.class);

        this.mockMvc.perform(get("/applications/{id}", 1L).accept(HAL_JSON))
            .andExpect(status().isNotFound());
    }

    @EnableSpringDataWebSupport
    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        ApplicationResourceAssembler applicationResourceAssembler(ChaosRepository chaosRepository) {
            return new ApplicationResourceAssembler(chaosRepository);
        }

    }

}
