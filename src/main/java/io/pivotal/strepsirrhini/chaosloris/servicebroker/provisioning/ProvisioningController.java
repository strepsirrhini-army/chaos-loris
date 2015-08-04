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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
final class ProvisioningController {

    private final String host;

    @Autowired
    ProvisioningController(@Value("${serviceBroker.host}") String host) {
        this.host = host;
    }

    // 201 Created	Service instance has been created. The expected response body is below.
    // 200 OK	May be returned if the service instance already exists and the requested parameters are identical to
    // the
    // existing service instance. The expected response body is below.
    // 409 Conflict	Should be returned if the requested service instance already exists. The expected response body is
    // “{}”
    @RequestMapping(method = RequestMethod.PUT, value = "/v2/service_instances/{instanceId}")
    ProvisioningResponse create(@PathVariable("instanceId") UUID instanceId,
                                @RequestBody ProvisioningRequest provisioningRequest) {
        URI dashboardUri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(this.host)
                .pathSegment("dashboard")
                .pathSegment(instanceId.toString())
                .build().toUri();

        return new ProvisioningResponse(dashboardUri);
    }

    //  200 OK	New plan is effective. The expected response body is `{}`.
    // 422 Unprocessable entity	May be returned if the particular plan change requested is not supported or if the
    // request can not currently be fulfilled due to the state of the instance (eg. instance utilization is over the
    // quota of the requested plan). Broker should include a user-facing message in the body; for details see Broker
    // Errors.
    @RequestMapping(method = RequestMethod.PATCH, value = "/v2/service_instances/{instanceId}")
    Map<?, ?> update(@PathVariable("instanceId") UUID instanceId,
                     @RequestBody UpdateRequest updateRequest) {
        return Collections.emptyMap();
    }

    // 200 OK	Service instance was deleted. The expected response body is “{}”
    // 410 Gone	Should be returned if the service instance does not exist. The expected response body is “{}”
    @RequestMapping(method = RequestMethod.DELETE, value = "/v2/service_instances/{instanceId}")
    Map<?, ?> delete(@PathVariable("instanceId") UUID instanceId,
                     @RequestParam("service_id") UUID serviceId,
                     @RequestParam("plan_id") UUID planId) {
        return Collections.emptyMap();
    }

}
