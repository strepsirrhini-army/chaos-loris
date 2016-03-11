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

import org.junit.Test;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class IndexControllerTest extends AbstractControllerTest {

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.applications").exists())
            .andExpect(jsonPath("$._links.chaoses").exists())
            .andExpect(jsonPath("$._links.events").exists())
            .andExpect(jsonPath("$._links.schedules").exists());
    }

}
