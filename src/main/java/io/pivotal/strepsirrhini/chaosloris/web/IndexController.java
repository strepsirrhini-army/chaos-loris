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

package io.pivotal.strepsirrhini.chaosloris.web;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class IndexController {

    @RequestMapping(method = GET, value = "/", produces = HAL_JSON_VALUE)
    ResponseEntity index() {
        ResourceSupport resource = new ResourceSupport();
        resource.add(linkTo(methodOn(IndexController.class).index()).withSelfRel());
        resource.add(linkTo(ApplicationController.class).withRel("applications"));
        resource.add(linkTo(ChaosController.class).withRel("chaoses"));
        resource.add(linkTo(EventController.class).withRel("events"));
        resource.add(linkTo(ScheduleController.class).withRel("schedules"));

        return ResponseEntity.ok(resource);
    }

}
