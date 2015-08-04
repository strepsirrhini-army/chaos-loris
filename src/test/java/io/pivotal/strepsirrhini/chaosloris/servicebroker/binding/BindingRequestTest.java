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

import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractDeserializationTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public final class BindingRequestTest extends AbstractDeserializationTest<BindingRequest> {

    public BindingRequestTest() {
        super(BindingRequest.class);
    }

    @Override
    protected void assertContents(BindingRequest instance) {
        assertEquals(UUID.fromString("9c0963b6-bbb2-4f04-b389-708eca7a3a54"), instance.getAppGuid());
        assertEquals(Collections.emptyMap(), instance.getParameters());
        assertEquals(UUID.fromString("03e17851-de4d-435c-beb2-6eb92a8c941d"), instance.getPlanId());
        assertEquals(UUID.fromString("f6fe01b7-1e27-4857-961f-8451b1248ad1"), instance.getServiceId());
    }

    @Override
    protected Map getMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("app_guid", UUID.fromString("9c0963b6-bbb2-4f04-b389-708eca7a3a54"));
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", UUID.fromString("03e17851-de4d-435c-beb2-6eb92a8c941d"));
        m.put("service_id", UUID.fromString("f6fe01b7-1e27-4857-961f-8451b1248ad1"));

        return m;
    }
}
