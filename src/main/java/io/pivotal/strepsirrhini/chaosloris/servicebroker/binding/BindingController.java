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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
final class BindingController {

    private final Credentials credentials;

    @Autowired
    BindingController(Credentials credentials) {
        this.credentials = credentials;
    }

    // 201 Created	Binding has been created. The expected response body is below.
    // 200 OK	May be returned if the binding already exists and the requested parameters are identical to the existing binding. The expected response body is below.
    // 409 Conflict	Should be returned if the requested binding already exists. The expected response body is `{}`, though the description field can be used to return a user-facing error message, as described in Broker Errors.
    // 422 Unprocessable Entity	Should be returned if the broker requires that app_guid be included in the request body. The expected response body is: { "error": "RequiresApp", "description": "This service supports generation of credentials through binding an application only." }
    @RequestMapping(method = RequestMethod.PUT,
            value = "/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    BindingResponse create(@PathVariable UUID instanceId, @PathVariable UUID bindingId,
                           @RequestBody BindingRequest bindingRequest) {
        return new BindingResponse(this.credentials, null);
    }

    // 200 OK	Binding was deleted. The expected response body is `{}`
    // 410 Gone	Should be returned if the binding does not exist. The expected response body is `{}`
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    Map<?, ?> delete(@PathVariable UUID instanceId, @PathVariable UUID bindingId,
                     @RequestParam("service_id") UUID serviceId, @RequestParam("plan_id") UUID planId) {
        return Collections.emptyMap();
    }

}
