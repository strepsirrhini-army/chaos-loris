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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

final class ProvisioningRequest {

    private final String serviceId;

    private final String planId;

    private final String organizationGuid;

    private final String spaceGuid;

    @JsonCreator
    ProvisioningRequest(@JsonProperty("service_id") String serviceId, @JsonProperty("plan_id") String planId,
                        @JsonProperty("organization_guid") String organizationGuid,
                        @JsonProperty("space_guid") String spaceGuid) {
        Assert.notNull(serviceId);
        Assert.notNull(planId);
        Assert.notNull(organizationGuid);
        Assert.notNull(spaceGuid);

        this.serviceId = serviceId;
        this.planId = planId;
        this.organizationGuid = organizationGuid;
        this.spaceGuid = spaceGuid;
    }

    String getServiceId() {
        return this.serviceId;
    }

    String getPlanId() {
        return this.planId;
    }

    String getOrganizationGuid() {
        return this.organizationGuid;
    }

    String getSpaceGuid() {
        return this.spaceGuid;
    }

}
