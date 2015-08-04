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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class BindingControllerTest extends AbstractControllerTest {

    @Test
    public void create() throws Exception {
        this.mockMvc.perform(
                put("/v2/service_instances/009b82a8-81ed-4a28-9430-ce10f6442b05/service_bindings/48764271-0e55-4567-8014-df50b4ea2056")
                        .content(payload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentials.*", hasSize(0)));
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(
                delete("/v2/service_instances/009b82a8-81ed-4a28-9430-ce10f6442b05/service_bindings/48764271-0e55-4567-8014-df50b4ea2056")
                        .param("plan_id", "03e17851-de4d-435c-beb2-6eb92a8c941d")
                        .param("service_id", "f6fe01b7-1e27-4857-961f-8451b1248ad1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    private String payload() throws JsonProcessingException {
        Map<String, Object> m = new HashMap<>();
        m.put("app_guid", UUID.fromString("9c0963b6-bbb2-4f04-b389-708eca7a3a54"));
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", UUID.fromString("03e17851-de4d-435c-beb2-6eb92a8c941d"));
        m.put("service_id", UUID.fromString("f6fe01b7-1e27-4857-961f-8451b1248ad1"));

        return this.objectMapper.writeValueAsString(m);
    }

}
