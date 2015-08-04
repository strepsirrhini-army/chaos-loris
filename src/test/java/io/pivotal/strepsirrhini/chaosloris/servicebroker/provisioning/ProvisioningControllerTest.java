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

package io.pivotal.strepsirrhini.chaosloris.servicebroker.provisioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ProvisioningControllerTest extends AbstractControllerTest {

    @Test
    public void create() throws Exception {
        this.mockMvc.perform(put("/v2/service_instances/0").content(createPayload()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dashboard_url")
                        .value("https://10.128.10.4/dashboard/test-organization-guid/test-space-guid"));
    }

    @Test
    public void testUpdate() throws Exception {
        this.mockMvc.perform(patch("/v2/service_instances/0").content(updatePayload()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(delete("/v2/service_instances/0")
                .param("service_id", "test-service-id").param("plan_id", "test-plan-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    private String createPayload() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("service_id", "test-service-id");
        m.put("plan_id", "test-plan-id");
        m.put("organization_guid", "test-organization-guid");
        m.put("space_guid", "test-space-guid");

        return this.objectMapper.writeValueAsString(m);
    }

    private String updatePayload() throws JsonProcessingException {
        Map<String, String> m = Collections.singletonMap("plan_id", "test-plan-id");

        return this.objectMapper.writeValueAsString(m);
    }

}
