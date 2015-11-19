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
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
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
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/chaoses")
class ChaosController {

    private static final Pattern APPLICATION = Pattern.compile(".*/applications/([\\d]+)");

    private static final Pattern SCHEDULE = Pattern.compile(".*/schedules/([\\d]+)");

    private final ApplicationRepository applicationRepository;

    private final ChaosRepository chaosRepository;

    private final ChaosResourceAssembler chaosResourceAssembler;

    private final ScheduleRepository scheduleRepository;

    @Autowired
    ChaosController(ApplicationRepository applicationRepository, ChaosRepository chaosRepository, ChaosResourceAssembler chaosResourceAssembler, ScheduleRepository scheduleRepository) {
        this.applicationRepository = applicationRepository;
        this.chaosRepository = chaosRepository;
        this.chaosResourceAssembler = chaosResourceAssembler;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    @RequestMapping(method = POST, value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody ChaosCreateInput input) {
        Application application = getApplication(input.getApplication());
        Schedule schedule = getSchedule(input.getSchedule());

        Chaos chaos = new Chaos(application, input.getProbability(), schedule);
        this.chaosRepository.saveAndFlush(chaos);

        return ResponseEntity
                .created(linkTo(methodOn(ChaosController.class).read(chaos.getId())).toUri())
                .build();
    }

    @Transactional
    @RequestMapping(method = DELETE, value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.chaosRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "", produces = HAL_JSON_VALUE)
    public ResponseEntity list(Pageable pageable, PagedResourcesAssembler<Chaos> pagedResourcesAssembler) {
        Page<Chaos> chaos = this.chaosRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(chaos, this.chaosResourceAssembler));
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, value = "/{id}", produces = HAL_JSON_VALUE)
    public ResponseEntity read(@PathVariable Long id) {
        Chaos chaos = this.chaosRepository.getOne(id);
        return ResponseEntity.ok(this.chaosResourceAssembler.toResource(chaos));
    }

    @Transactional
    @RequestMapping(method = PATCH, value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable Long id, @Valid @RequestBody ChaosUpdateInput input) {
        Chaos chaos = this.chaosRepository.getOne(id);

        if (input.getProbability() != null) {
            chaos.setProbability(input.getProbability());
        }

        this.chaosRepository.save(chaos);
        return ResponseEntity.noContent().build();
    }

    private Application getApplication(URI uri) {
        Matcher matcher = APPLICATION.matcher(uri.toASCIIString());
        if (matcher.find()) {
            return this.applicationRepository.getOne(Long.valueOf(matcher.group(1)));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Schedule getSchedule(URI uri) {
        Matcher matcher = SCHEDULE.matcher(uri.toASCIIString());
        if (matcher.find()) {
            return this.scheduleRepository.getOne(Long.valueOf(matcher.group(1)));
        } else {
            throw new IllegalArgumentException();
        }
    }

}
