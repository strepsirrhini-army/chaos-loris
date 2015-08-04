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

import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractDeserializationTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public final class ProvisioningRequestTest extends AbstractDeserializationTest<ProvisioningRequest> {

    public ProvisioningRequestTest() {
        super(ProvisioningRequest.class);
    }

    @Override
    protected void assertContents(ProvisioningRequest instance) {
        assertEquals(UUID.fromString("356f58e5-0abe-4674-972e-d156d2065a9b"), instance.getOrganizationGuid());
        assertEquals(Collections.emptyMap(), instance.getParameters());
        assertEquals(UUID.fromString("03e17851-de4d-435c-beb2-6eb92a8c941d"), instance.getPlanId());
        assertEquals(UUID.fromString("f6fe01b7-1e27-4857-961f-8451b1248ad1"), instance.getServiceId());
        assertEquals(UUID.fromString("a65bc9e3-edd6-472b-85df-3cc68d6d8705"), instance.getSpaceGuid());
    }

    @Override
    protected Map getMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("organization_guid", UUID.fromString("356f58e5-0abe-4674-972e-d156d2065a9b"));
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", UUID.fromString("03e17851-de4d-435c-beb2-6eb92a8c941d"));
        m.put("service_id", UUID.fromString("f6fe01b7-1e27-4857-961f-8451b1248ad1"));
        m.put("space_guid", UUID.fromString("a65bc9e3-edd6-472b-85df-3cc68d6d8705"));

        return m;
    }
}
