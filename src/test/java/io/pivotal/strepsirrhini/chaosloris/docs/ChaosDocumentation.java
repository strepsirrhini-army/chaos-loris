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

package io.pivotal.strepsirrhini.chaosloris.docs;

import io.pivotal.strepsirrhini.chaosloris.MapBuilder;
import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.web.ApplicationController;
import io.pivotal.strepsirrhini.chaosloris.web.ChaosCreateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ChaosUpdateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class ChaosDocumentation extends AbstractApiDocumentation {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void chaosCreate() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(ChaosCreateInput.class);

        this.document.snippets(
            requestFields(
                fields.withPath("application").description("The URI of the application to create chaos on"),
                fields.withPath("probability").description("The probability of an instance of the application experiencing chaos"),
                fields.withPath("schedule").description("The URI of the schedule to create chaos on")),
            responseHeaders(
                headerWithName("Location").description("The URI of the newly created chaos")));

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        String content = asJson(MapBuilder.builder()
            .entry("application", linkTo(methodOn(ApplicationController.class).read(application.getId())).toUri())
            .entry("probability", 0.1)
            .entry("schedule", linkTo(methodOn(ScheduleController.class).read(schedule.getId())).toUri())
            .build());

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON).content(content));
    }

    @Test
    public void chaosDelete() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The chaos' id")));

        this.mockMvc.perform(delete("/chaoses/{id}", chaos.getId()));
    }

    @Test
    public void chaosList() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        String query = createPages(i -> {
            Application application = new Application(UUID.randomUUID());
            this.applicationRepository.saveAndFlush(application);

            Chaos chaos = new Chaos(application, 0.1, schedule);
            this.chaosRepository.saveAndFlush(chaos);
        });

        this.document.snippets(
            requestParameters(
                parameterWithName("page").description("Page to retrieve"),
                parameterWithName("size").description("Size of the page to retrieve")),
            responseFields(
                fieldWithPath("page.number").description("The number of this page of results"),
                fieldWithPath("page.size").description("The size of this page of results"),
                fieldWithPath("page.totalPages").description("The total number of pages of results"),
                fieldWithPath("page.totalElements").description("The total number of results"),
                fieldWithPath("_embedded.chaoses").description("A collection of Chaoses as described in [Read a Chaos](#read-a-chaos)"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("first").optional().description("The first page of results"),
                linkWithRel("last").optional().description("The last page of results"),
                linkWithRel("next").optional().description("The next page of results"),
                linkWithRel("prev").optional().description("The previous page of results")));

        this.mockMvc.perform(get("/chaoses" + query).accept(HAL_JSON));
    }

    @Test
    public void chaosRead() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Event event = new Event(chaos, Instant.now(), Collections.singletonList(3), 10);
        this.eventRepository.saveAndFlush(event);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The chaos' id")),
            responseFields(
                fieldWithPath("probability").description("The probability of an instance of the application experiencing chaos"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("application").description("The [Application](#applications) to create chaos on"),
                linkWithRel("event").description("The [Events](#events) performed by this chaos"),
                linkWithRel("schedule").description("The [Schedule](#schedules) to create chaos on")));

        this.mockMvc.perform(get("/chaoses/{id}", chaos.getId()).accept(HAL_JSON));
    }

    @Test
    public void chaosUpdate() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        ConstrainedFields fields = new ConstrainedFields(ChaosUpdateInput.class);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The chaos' id")),
            requestFields(
                fields.withPath("probability").optional().type(NUMBER).description("The probability of an instance of the application experiencing chaos")));

        String content = asJson(MapBuilder.builder()
            .entry("probability", 0.5)
            .build());

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON).content(content));
    }

}
