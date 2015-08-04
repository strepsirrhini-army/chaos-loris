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

final class BindingRequest {

    private final String serviceId;

    private final String planId;

    private final String appGuid;

    @JsonCreator
    BindingRequest(@JsonProperty("service_id") String serviceId, @JsonProperty("plan_id") String planId,
                   @JsonProperty("app_guid") String appGuid) {
        Assert.notNull(serviceId);
        Assert.notNull(planId);
        Assert.notNull(appGuid);

        this.serviceId = serviceId;
        this.planId = planId;
        this.appGuid = appGuid;
    }

    String getServiceId() {
        return this.serviceId;
    }

    String getPlanId() {
        return this.planId;
    }

    String getAppGuid() {
        return this.appGuid;
    }

}
