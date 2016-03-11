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
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
final class ApplicationResourceAssembler extends ResourceAssemblerSupport<Application, ApplicationResourceAssembler.ApplicationResource> {

    private final ChaosRepository chaosRepository;

    @Autowired
    ApplicationResourceAssembler(ChaosRepository chaosRepository) {
        super(ApplicationController.class, ApplicationResource.class);
        this.chaosRepository = chaosRepository;
    }

    @Override
    public ApplicationResource toResource(Application application) {
        ApplicationResource resource = createResourceWithId(application.getId(), application);

        this.chaosRepository.findByApplication(application).stream()
            .map(chaos -> linkTo(methodOn(ChaosController.class).read(chaos.getId())).withRel("chaos"))
            .forEach(resource::add);

        return resource;
    }

    @Override
    protected ApplicationResource instantiateResource(Application application) {
        return new ApplicationResource(application);
    }

    static final class ApplicationResource extends Resource<Application> {

        private ApplicationResource(Application application) {
            super(application);
        }

    }

}
