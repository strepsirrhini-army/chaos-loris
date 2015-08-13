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

package io.pivotal.strepsirrhini.chaosloris.servicebroker.binding;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.pivotal.strepsirrhini.chaosloris.model.Binding;
import io.pivotal.strepsirrhini.chaosloris.model.BindingRepository;
import io.pivotal.strepsirrhini.chaosloris.model.Instance;
import io.pivotal.strepsirrhini.chaosloris.model.InstanceRepository;
import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.pivotal.strepsirrhini.chaosloris.TestIds.ALTERNATE_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.APPLICATION_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.BINDING_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.INSTANCE_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.ORGANIZATION_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.PLAN_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.SERVICE_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.SPACE_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BindingControllerTest extends AbstractControllerTest {

    @Autowired
    private volatile BindingRepository bindingRepository;

    @Autowired
    private volatile InstanceRepository instanceRepository;

    private volatile Instance instance;

    @Before
    public void createInstance() throws Exception {
        this.instance = this.instanceRepository.saveAndFlush(new Instance(INSTANCE_ID, ORGANIZATION_ID,
                Collections.emptyMap(), SPACE_ID));
    }

    @Test
    public void create() throws Exception {
        this.mockMvc.perform(
                put("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .content(createPayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*", hasSize(1)));

        assertEquals(1, this.bindingRepository.count());
    }

    @Test
    public void createAlreadyExistsNoConflict() throws Exception {
        this.bindingRepository.saveAndFlush(new Binding(BINDING_ID, this.instance, APPLICATION_ID,
                Collections.emptyMap()));

        this.mockMvc.perform(
                put("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .content(createPayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        assertEquals(1, this.bindingRepository.count());
    }

    @Test
    public void createAlreadyExistsConflict() throws Exception {
        this.bindingRepository.saveAndFlush(new Binding(BINDING_ID, this.instance, ALTERNATE_ID,
                Collections.emptyMap()));

        this.mockMvc.perform(
                put("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .content(createAlternatePayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    public void createMissingApplicationId() throws Exception {
        this.bindingRepository.saveAndFlush(new Binding(BINDING_ID, this.instance, APPLICATION_ID,
                Collections.emptyMap()));

        this.mockMvc.perform(
                put("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .content(createMissingApplicationIdPayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("RequiresApp"))
                .andExpect(jsonPath("$.description")
                        .value("This service supports generation of credentials through binding an application only."));
    }

    @Test
    public void testDelete() throws Exception {
        this.bindingRepository.saveAndFlush(new Binding(BINDING_ID, this.instance, APPLICATION_ID,
                Collections.emptyMap()));

        this.mockMvc.perform(
                delete("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .param("plan_id", PLAN_ID.toString())
                        .param("service_id", SERVICE_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));

        assertEquals(1, this.instanceRepository.count());
        assertEquals(0, this.bindingRepository.count());
    }

    @Test
    public void testDeleteDoesNotExist() throws Exception {
        this.mockMvc.perform(
                delete("/v2/service_instances/" + INSTANCE_ID + "/service_bindings/" + BINDING_ID)
                        .param("plan_id", PLAN_ID.toString())
                        .param("service_id", SERVICE_ID.toString()))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    private String createAlternatePayload() throws JsonProcessingException {
        Map<String, Object> m = new HashMap<>();
        m.put("app_guid", APPLICATION_ID);
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", PLAN_ID);
        m.put("service_id", ALTERNATE_ID);

        return this.objectMapper.writeValueAsString(m);
    }


    private String createMissingApplicationIdPayload() throws JsonProcessingException {
        Map<String, Object> m = new HashMap<>();
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", PLAN_ID);
        m.put("service_id", SERVICE_ID);

        return this.objectMapper.writeValueAsString(m);
    }

    private String createPayload() throws JsonProcessingException {
        Map<String, Object> m = new HashMap<>();
        m.put("app_guid", APPLICATION_ID);
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", PLAN_ID);
        m.put("service_id", SERVICE_ID);

        return this.objectMapper.writeValueAsString(m);
    }

}
