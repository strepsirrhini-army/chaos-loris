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

import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleCreatedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleDeletedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ScheduleRepository scheduleRepository;

    private final ScheduleResourceAssembler scheduleResourceAssembler;

    @Autowired
    ScheduleController(ApplicationEventPublisher applicationEventPublisher, ScheduleRepository scheduleRepository, ScheduleResourceAssembler scheduleResourceAssembler) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.scheduleRepository = scheduleRepository;
        this.scheduleResourceAssembler = scheduleResourceAssembler;
    }

    @Transactional
    @RequestMapping(method = POST, value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody ScheduleCreateInput input) {
        Schedule schedule = new Schedule(input.getExpression(), input.getName());
        this.scheduleRepository.saveAndFlush(schedule);
        this.applicationEventPublisher.publishEvent(new ScheduleCreatedEvent(this, schedule));

        return ResponseEntity
                .created(linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
                .build();
    }

    @Transactional
    @RequestMapping(method = DELETE, value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.scheduleRepository.delete(id);
        this.applicationEventPublisher.publishEvent(new ScheduleDeletedEvent(this, id));

        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "", produces = HAL_JSON_VALUE)
    public ResponseEntity list(Pageable pageable, PagedResourcesAssembler<Schedule> pagedResourcesAssembler) {
        Page<Schedule> schedules = this.scheduleRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(schedules, this.scheduleResourceAssembler));
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "/{id}", produces = HAL_JSON_VALUE)
    public ResponseEntity read(@PathVariable Long id) {
        Schedule schedule = this.scheduleRepository.getOne(id);
        return ResponseEntity.ok(this.scheduleResourceAssembler.toResource(schedule));
    }

    @Transactional
    @RequestMapping(method = PATCH, value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable Long id, @Valid @RequestBody ScheduleUpdateInput input) {
        Schedule schedule = this.scheduleRepository.getOne(id);

        if (input.getExpression() != null) {
            schedule.setExpression(input.getExpression());
        }

        if (input.getName() != null) {
            schedule.setName(input.getName());
        }

        this.scheduleRepository.save(schedule);
        this.applicationEventPublisher.publishEvent(new ScheduleUpdatedEvent(this, schedule));

        return ResponseEntity.noContent().build();
    }

}
