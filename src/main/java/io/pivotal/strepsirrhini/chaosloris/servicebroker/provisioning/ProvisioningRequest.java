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

import java.util.Map;
import java.util.UUID;

final class ProvisioningRequest {

    private final UUID organizationGuid;

    private final Map<String, ?> parameters;

    private final UUID planId;

    private final UUID serviceId;

    private final UUID spaceGuid;

    @JsonCreator
    ProvisioningRequest(@JsonProperty("organization_guid") UUID organizationGuid,
                        @JsonProperty("parameters") Map<String, ?> parameters,
                        @JsonProperty("plan_id") UUID planId,
                        @JsonProperty("service_id") UUID serviceId,
                        @JsonProperty("space_guid") UUID spaceGuid) {
        Assert.notNull(serviceId);
        Assert.notNull(planId);
        Assert.notNull(organizationGuid);
        Assert.notNull(spaceGuid);

        this.organizationGuid = organizationGuid;
        this.parameters = parameters;
        this.planId = planId;
        this.serviceId = serviceId;
        this.spaceGuid = spaceGuid;
    }

    UUID getOrganizationGuid() {
        return this.organizationGuid;
    }

    Map<String, ?> getParameters() {
        return this.parameters;
    }

    UUID getPlanId() {
        return this.planId;
    }

    UUID getServiceId() {
        return this.serviceId;
    }

    UUID getSpaceGuid() {
        return this.spaceGuid;
    }

}
