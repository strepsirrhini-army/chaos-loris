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

import io.pivotal.strepsirrhini.chaosloris.model.Instance;
import io.pivotal.strepsirrhini.chaosloris.model.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.OK;

@RestController
final class ProvisioningController {

    private final String host;

    private final InstanceRepository instanceRepository;

    @Autowired
    ProvisioningController(@Value("${serviceBroker.host}") String host, InstanceRepository instanceRepository) {
        this.host = host;
        this.instanceRepository = instanceRepository;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, value = "/v2/service_instances/{instanceId}")
    ResponseEntity<ProvisioningResponse> create(@PathVariable("instanceId") UUID instanceId,
                                                @RequestBody ProvisioningRequest provisioningRequest) {
        Instance newInstance = new Instance(instanceId, provisioningRequest.getOrganizationGuid(),
                provisioningRequest.getParameters(), provisioningRequest.getPlanId(), provisioningRequest
                .getServiceId(), provisioningRequest.getSpaceGuid());

        Instance previousInstance = this.instanceRepository.findOne(instanceId);
        if (previousInstance == null) {
            this.instanceRepository.save(newInstance);

            ProvisioningResponse provisioningResponse = new ProvisioningResponse(getDashboardUri(instanceId));
            return new ResponseEntity<>(provisioningResponse, CREATED);
        } else if (newInstance.equals(previousInstance)) {
            ProvisioningResponse provisioningResponse = new ProvisioningResponse(getDashboardUri(instanceId));
            return new ResponseEntity<>(provisioningResponse, OK);
        } else {
            ProvisioningResponse provisioningResponse = new ProvisioningResponse();
            return new ResponseEntity<>(provisioningResponse, CONFLICT);
        }
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, value = "/v2/service_instances/{instanceId}")
    Map<?, ?> update(@PathVariable("instanceId") UUID instanceId,
                     @RequestBody UpdateRequest updateRequest) {
        return Collections.emptyMap();
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/v2/service_instances/{instanceId}")
    ResponseEntity<Map<?, ?>> delete(@PathVariable("instanceId") UUID instanceId,
                                     @RequestParam("service_id") UUID serviceId,
                                     @RequestParam("plan_id") UUID planId) {
        if (this.instanceRepository.exists(instanceId)) {
            this.instanceRepository.delete(instanceId);
            return new ResponseEntity<>(Collections.emptyMap(), OK);
        } else {
            return new ResponseEntity<>(Collections.emptyMap(), GONE);
        }
    }

    private URI getDashboardUri(UUID instanceId) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(this.host)
                .pathSegment("dashboard")
                .pathSegment(instanceId.toString())
                .build().toUri();
    }

}
