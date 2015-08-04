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
import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class BindingControllerTest extends AbstractControllerTest {

    @Test
    public void create() throws Exception {
        this.mockMvc.perform(put("/v2/service_instances/0/service_bindings/1").content(payload())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentials.*", hasSize(0)));
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(delete("/v2/service_instances/0/service_bindings/1")
                .param("service_id", "test-service-id").param("plan_id", "test-plan-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    private String payload() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("service_id", "test-service-id");
        m.put("plan_id", "test-plan-id");
        m.put("app_guid", "test-app-guid");

        return this.objectMapper.writeValueAsString(m);
    }

}
