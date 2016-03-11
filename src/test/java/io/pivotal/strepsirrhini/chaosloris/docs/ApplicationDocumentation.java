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
import io.pivotal.strepsirrhini.chaosloris.web.ApplicationCreateInput;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class ApplicationDocumentation extends AbstractApiDocumentation {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void applicationCreate() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(ApplicationCreateInput.class);

        this.document.snippets(
            requestFields(
                fields.withPath("applicationId").description("The Cloud Foundry application id")),
            responseHeaders(
                headerWithName("Location").description("The URI of the newly created application")));

        String content = asJson(MapBuilder.builder()
            .entry("applicationId", UUID.randomUUID().toString())
            .build());

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON).content(content));
    }

    @Test
    public void applicationDelete() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The application's id")));

        this.mockMvc.perform(delete("/applications/{id}", application.getId()));
    }

    @Test
    public void applicationList() throws Exception {
        String query = createPages(i -> {
            Application application = new Application(UUID.randomUUID());
            this.applicationRepository.saveAndFlush(application);
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
                fieldWithPath("_embedded.applications").description("A collection of Applications as described in [Read an Application](#read-an-application)"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("first").optional().description("The first page of results"),
                linkWithRel("last").optional().description("The last page of results"),
                linkWithRel("next").optional().description("The next page of results"),
                linkWithRel("prev").optional().description("The previous page of results")));

        this.mockMvc.perform(get("/applications" + query).accept(HAL_JSON));
    }

    @Test
    public void applicationRead() throws Exception {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule1 = new Schedule("0 0 * * * *", "hourly");
        this.scheduleRepository.saveAndFlush(schedule1);

        Chaos chaos1 = new Chaos(application, 0.1, schedule1);
        this.chaosRepository.saveAndFlush(chaos1);

        Schedule schedule2 = new Schedule("0 0 0 * * *", "daily");
        this.scheduleRepository.saveAndFlush(schedule2);

        Chaos chaos2 = new Chaos(application, 0.1, schedule2);
        this.chaosRepository.saveAndFlush(chaos2);

        this.document.snippets(
            pathParameters(
                parameterWithName("id").description("The application's id")),
            responseFields(
                fieldWithPath("applicationId").description("The Cloud Foundry application id"),
                fieldWithPath("_links").ignored()),
            links(
                linkWithRel("self").ignored(),
                linkWithRel("chaos").description("[Chaos](#chaoses) instances using this application")));

        this.mockMvc.perform(get("/applications/{id}", application.getId()).accept(HAL_JSON));
    }

}
