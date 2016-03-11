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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;

    private final ApplicationResourceAssembler applicationResourceAssembler;

    @Autowired
    ApplicationController(ApplicationRepository applicationRepository, ApplicationResourceAssembler applicationResourceAssembler) {
        this.applicationRepository = applicationRepository;
        this.applicationResourceAssembler = applicationResourceAssembler;
    }

    @Transactional
    @RequestMapping(method = POST, value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody ApplicationCreateInput input) {
        Application application = new Application(input.getApplicationId());
        this.applicationRepository.saveAndFlush(application);

        return ResponseEntity
            .created(linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
            .build();
    }

    @Transactional
    @RequestMapping(method = DELETE, value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.applicationRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "", produces = HAL_JSON_VALUE)
    public ResponseEntity list(Pageable pageable, PagedResourcesAssembler<Application> pagedResourcesAssembler) {
        Page<Application> applications = this.applicationRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(applications, this.applicationResourceAssembler));
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "/{id}", produces = HAL_JSON_VALUE)
    public ResponseEntity read(@PathVariable Long id) {
        Application application = this.applicationRepository.getOne(id);
        return ResponseEntity.ok(this.applicationResourceAssembler.toResource(application));
    }

}
