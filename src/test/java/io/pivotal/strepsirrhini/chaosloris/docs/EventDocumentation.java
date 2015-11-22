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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.ApplicationRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class EventDocumentation extends AbstractApiDocumentation {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void eventDelete() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        Event event = new Event(chaos, Instant.now(), Collections.singletonList(3), 10);
        this.eventRepository.saveAndFlush(event);

        this.document.snippets(
                pathParameters(
                        parameterWithName("id").description("The event's id")));

        this.mockMvc.perform(delete("/events/{id}", event.getId()));
    }

    @Test
    public void eventList() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos);

        String query = createPages(i -> {
            Event event = new Event(chaos, Instant.now(), Collections.singletonList(i % 10), 10);
            this.eventRepository.saveAndFlush(event);
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
                        fieldWithPath("_embedded.events").description("A collection of Events as described in [Read an Event](#read-an-event)"),
                        fieldWithPath("_links").ignored()),
                links(
                        linkWithRel("self").ignored(),
                        linkWithRel("first").optional().description("The first page of results"),
                        linkWithRel("last").optional().description("The last page of results"),
                        linkWithRel("next").optional().description("The next page of results"),
                        linkWithRel("prev").optional().description("The previous page of results")));

        this.mockMvc.perform(get("/events" + query).accept(HAL_JSON));
    }

    @Test
    public void eventRead() throws Exception {
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
                        parameterWithName("id").description("The event's id")),
                responseFields(
                        fieldWithPath("executedAt").description("An ISO-8601 timestamp for when the event occurred"),
                        fieldWithPath("terminatedInstances").description("The instances terminated during the event"),
                        fieldWithPath("totalInstanceCount").description("The total number of instances that were candidates for termination during the event"),
                        fieldWithPath("terminatedInstanceCount").description("The total number of instances terminated during the event"),
                        fieldWithPath("_links").ignored()),
                links(
                        linkWithRel("self").ignored(),
                        linkWithRel("chaos").description("The [Chaos](#chaoses) that triggered the event")));

        this.mockMvc.perform(get("/events/{id}", event.getId()).accept(HAL_JSON));
    }

}
