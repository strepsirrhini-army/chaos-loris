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

import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;

    private final EventResourceAssembler eventResourceAssembler;

    @Autowired
    EventController(EventRepository eventRepository, EventResourceAssembler eventResourceAssembler) {
        this.eventRepository = eventRepository;
        this.eventResourceAssembler = eventResourceAssembler;
    }

    @Transactional
    @RequestMapping(method = DELETE, value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.eventRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "", produces = HAL_JSON_VALUE)
    public ResponseEntity list(Pageable pageable, PagedResourcesAssembler<Event> pagedResourcesAssembler) {
        Page<Event> events = this.eventRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(events, this.eventResourceAssembler));
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "/{id}", produces = HAL_JSON_VALUE)
    public ResponseEntity read(@PathVariable Long id) {
        Event event = this.eventRepository.getOne(id);
        return ResponseEntity.ok(this.eventResourceAssembler.toResource(event));
    }

}
