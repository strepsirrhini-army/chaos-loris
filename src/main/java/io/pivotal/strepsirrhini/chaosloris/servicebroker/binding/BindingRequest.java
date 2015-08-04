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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

final class BindingRequest {

    private final UUID appGuid;

    private final Map<String, ?> parameters;

    private final UUID planId;

    private final UUID serviceId;

    @JsonCreator
    BindingRequest(@JsonProperty("app_guid") UUID appGuid,
                   @JsonProperty("parameters") Map<String, ?> parameters,
                   @JsonProperty("plan_id") UUID planId,
                   @JsonProperty("service_id") UUID serviceId) {
        Assert.notNull(serviceId);
        Assert.notNull(planId);

        this.appGuid = appGuid;
        this.parameters = parameters;
        this.planId = planId;
        this.serviceId = serviceId;
    }

    UUID getAppGuid() {
        return this.appGuid;
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

}
