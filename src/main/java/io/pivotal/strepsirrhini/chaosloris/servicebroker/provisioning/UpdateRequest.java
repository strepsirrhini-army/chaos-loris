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

final class UpdateRequest {

    private final Map<String, ?> parameters;

    private final UUID planId;

    private final PreviousValues previousValues;

    private final UUID serviceId;

    @JsonCreator
    UpdateRequest(@JsonProperty("parameters") Map<String, ?> parameters,
                  @JsonProperty("plan_id") UUID planId,
                  @JsonProperty("previous_values") PreviousValues previousValues,
                  @JsonProperty("service_id") UUID serviceId) {
        Assert.notNull(serviceId);

        this.parameters = parameters;
        this.planId = planId;
        this.previousValues = previousValues;
        this.serviceId = serviceId;
    }

    Map<String, ?> getParameters() {
        return this.parameters;
    }

    UUID getPlanId() {
        return this.planId;
    }

    PreviousValues getPreviousValues() {
        return this.previousValues;
    }

    UUID getServiceId() {
        return this.serviceId;
    }

    static final class PreviousValues {

        private final UUID organizationId;

        private final UUID planId;

        private final UUID serviceId;

        private final UUID spaceId;

        @JsonCreator
        PreviousValues(@JsonProperty("organization_id") UUID organizationId,
                       @JsonProperty("plan_id") UUID planId,
                       @JsonProperty("service_id") UUID serviceId,
                       @JsonProperty("space_id") UUID spaceId) {
            this.organizationId = organizationId;
            this.planId = planId;
            this.serviceId = serviceId;
            this.spaceId = spaceId;
        }

        UUID getOrganizationId() {
            return this.organizationId;
        }

        UUID getPlanId() {
            return this.planId;
        }

        UUID getServiceId() {
            return this.serviceId;
        }

        UUID getSpaceId() {
            return this.spaceId;
        }

    }

}
