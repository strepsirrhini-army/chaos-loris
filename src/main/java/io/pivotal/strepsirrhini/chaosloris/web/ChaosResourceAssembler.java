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

import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
final class ChaosResourceAssembler extends ResourceAssemblerSupport<Chaos, ChaosResourceAssembler.ChaosResource> {

    private final EventRepository eventRepository;

    @Autowired
    ChaosResourceAssembler(EventRepository eventRepository) {
        super(ChaosController.class, ChaosResource.class);
        this.eventRepository = eventRepository;
    }

    @Override
    public ChaosResource toResource(Chaos chaos) {
        ChaosResource resource = createResourceWithId(chaos.getId(), chaos);

        resource.add(linkTo(methodOn(ApplicationController.class).read(chaos.getApplication().getId())).withRel("application"));
        resource.add(linkTo(methodOn(ScheduleController.class).read(chaos.getSchedule().getId())).withRel("schedule"));

        this.eventRepository.findByChaos(chaos).stream()
                .map(event -> linkTo(methodOn(EventController.class).read(event.getId())).withRel("event"))
                .forEach(resource::add);

        return resource;
    }

    @Override
    protected ChaosResource instantiateResource(Chaos chaos) {
        return new ChaosResource(chaos);
    }

    static final class ChaosResource extends Resource<Chaos> {

        private ChaosResource(Chaos chaos) {
            super(chaos);
        }

    }

}
