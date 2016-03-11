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
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleCreateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleUpdateInput;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class ScheduleDocumentation extends AbstractApiDocumentation {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void scheduleCreate() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(ScheduleCreateInput.class);

        this.document.snippets(
            requestFields(
                fields.withPath("expression").description("The [CRON expression](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) of the schedule"),
                fields.withPath("name").description("The user-readable name of the schedule")),
            responseHeaders(
                headerWithName("Location").description("The URI of the newly created schedule")));

        String content = asJson(MapBuilder.builder()
            .entry("expression", "0 0 * * * *")
            .entry("name", "hourly")
            .build());

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON).content(content));
    }

    @Test
    public void scheduleDelete() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The schedule's id")));

        this.mockMvc.perform(delete("/schedules/{id}", schedule.getId()));
    }

    @Test
    public void scheduleList() throws Exception {
        String query = createPages(i -> {
            Schedule schedule = new Schedule("0 0 * * * *", "hourly-" + i);
            this.scheduleRepository.saveAndFlush(schedule);
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
                fieldWithPath("_embedded.schedules").description("A collection of Schedules as described in [Read a Schedule](#read-a-schedule)"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("first").optional().description("The first page of results"),
                linkWithRel("last").optional().description("The last page of results"),
                linkWithRel("next").optional().description("The next page of results"),
                linkWithRel("prev").optional().description("The previous page of results")));

        this.mockMvc.perform(get("/schedules" + query).accept(HAL_JSON));
    }

    @Test
    public void scheduleRead() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application1 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application1);

        Chaos chaos1 = new Chaos(application1, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos1);

        Application application2 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application2);

        Chaos chaos2 = new Chaos(application2, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos2);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The schedule's id")),
            responseFields(
                fieldWithPath("expression").description("The [CRON expression](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) of the schedule"),
                fieldWithPath("name").description("The user-readable name of the schedule"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("chaos").description("[Chaos](#chaoses) instances using this schedule")));

        this.mockMvc.perform(get("/schedules/{id}", schedule.getId()).accept(HAL_JSON));
    }

    @Test
    public void scheduleUpdate() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule);

        ConstrainedFields fields = new ConstrainedFields(ScheduleUpdateInput.class);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The schedule's id")),
            requestFields(
                fields.withPath("expression").optional().type(STRING).description("The [CRON expression](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) of the schedule"),
                fields.withPath("name").optional().type(STRING).description("The user-readable name of the schedule")));

        String content = asJson(MapBuilder.builder()
            .entry("name", "default")
            .build());

        this.mockMvc.perform(patch("/schedules/{id}", schedule.getId()).contentType(APPLICATION_JSON).content(content));
    }

}
