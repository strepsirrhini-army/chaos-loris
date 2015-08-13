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

import io.pivotal.strepsirrhini.chaosloris.model.Binding;
import io.pivotal.strepsirrhini.chaosloris.model.BindingRepository;
import io.pivotal.strepsirrhini.chaosloris.model.Instance;
import io.pivotal.strepsirrhini.chaosloris.model.InstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.servicebroker.Errors.REQUIRES_APPLICATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
final class BindingController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BindingRepository bindingRepository;

    private final InstanceRepository instanceRepository;

    private final Credentials credentials;

    @Autowired
    BindingController(BindingRepository bindingRepository, InstanceRepository instanceRepository,
                      Credentials credentials) {
        this.bindingRepository = bindingRepository;
        this.instanceRepository = instanceRepository;
        this.credentials = credentials;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT,
            value = "/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    ResponseEntity<?> create(@PathVariable UUID instanceId, @PathVariable UUID bindingId,
                             @RequestBody BindingRequest bindingRequest) {
        this.logger.info("Binding {} to {}", instanceId, bindingId);

        Instance instance = this.instanceRepository.getOne(instanceId);

        Binding newBinding = new Binding(bindingId, instance, bindingRequest.getAppGuid(),
                bindingRequest.getParameters());

        Binding previousBinding = this.bindingRepository.findOne(bindingId);
        if (newBinding.getApplicationId() == null) {
            return new ResponseEntity<>(REQUIRES_APPLICATION, UNPROCESSABLE_ENTITY);
        } else if (previousBinding == null) {
            this.bindingRepository.save(newBinding);

            BindingResponse bindingResponse = new BindingResponse(this.credentials);
            return new ResponseEntity<>(bindingResponse, CREATED);
        } else if (newBinding.equals(previousBinding)) {
            BindingResponse bindingResponse = new BindingResponse(this.credentials);
            return new ResponseEntity<>(bindingResponse, OK);
        } else {
            BindingResponse bindingResponse = new BindingResponse();
            return new ResponseEntity<>(bindingResponse, CONFLICT);
        }
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    ResponseEntity<Map<?, ?>> delete(@PathVariable UUID instanceId, @PathVariable UUID bindingId,
                                     @RequestParam("service_id") UUID serviceId, @RequestParam("plan_id") UUID planId) {
        this.logger.info("Unbinding {} from {}", instanceId, bindingId);

        if (this.bindingRepository.exists(bindingId)) {
            this.bindingRepository.delete(bindingId);
            return new ResponseEntity<>(Collections.emptyMap(), OK);
        } else {
            return new ResponseEntity<>(Collections.emptyMap(), GONE);
        }
    }

}
