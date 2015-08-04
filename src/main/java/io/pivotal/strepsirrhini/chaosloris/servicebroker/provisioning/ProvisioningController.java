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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
final class ProvisioningController {

    private final String host;

    @Autowired
    ProvisioningController(@Value("${serviceBroker.host}") String host) {
        this.host = host;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/v2/service_instances/*")
    ProvisioningResponse create(@RequestBody ProvisioningRequest provisioningRequest) {
        URI dashboardUri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(this.host)
                .pathSegment("dashboard")
                .pathSegment(provisioningRequest.getOrganizationGuid())
                .pathSegment(provisioningRequest.getSpaceGuid())
                .build().toUri();

        return new ProvisioningResponse(dashboardUri);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/v2/service_instances/*")
    Map<?, ?> update(@RequestBody UpdateRequest updateRequest) {
        return Collections.emptyMap();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/v2/service_instances/*")
    Map<?, ?> delete(@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) {
        return Collections.emptyMap();
    }

}
