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

import static io.pivotal.strepsirrhini.chaosloris.TestIds.ORGANIZATION_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.PLAN_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.SERVICE_ID;
import static io.pivotal.strepsirrhini.chaosloris.TestIds.SPACE_ID;
import static org.junit.Assert.assertEquals;

public final class ProvisioningRequestTest extends AbstractDeserializationTest<ProvisioningRequest> {

    public ProvisioningRequestTest() {
        super(ProvisioningRequest.class);
    }

    @Override
    protected void assertContents(ProvisioningRequest instance) {
        assertEquals(ORGANIZATION_ID, instance.getOrganizationGuid());
        assertEquals(Collections.emptyMap(), instance.getParameters());
        assertEquals(PLAN_ID, instance.getPlanId());
        assertEquals(SERVICE_ID, instance.getServiceId());
        assertEquals(SPACE_ID, instance.getSpaceGuid());
    }

    @Override
    protected Map getMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("organization_guid", ORGANIZATION_ID);
        m.put("parameters", Collections.emptyMap());
        m.put("plan_id", PLAN_ID);
        m.put("service_id", SERVICE_ID);
        m.put("space_guid", SPACE_ID);

        return m;
    }
}
