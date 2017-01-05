/*
 * Copyright 2015-2017 the original author or authors.
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
import io.pivotal.strepsirrhini.chaosloris.web.ChaosController;
import io.pivotal.strepsirrhini.chaosloris.web.ChaosCreateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ChaosResourceAssembler;
import io.pivotal.strepsirrhini.chaosloris.web.ChaosUpdateInput;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.JsonTestUtilities.asJson;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.document;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.links;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listLinks;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listRequestParameters;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listResponseFields;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.page;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.query;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.responseFields;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriScheme = "https", uriHost = "chaos-loris", uriPort = 443)
@RunWith(SpringRunner.class)
@WebMvcTest(ChaosController.class)
public class ChaosDocumentation {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ApplicationRepository applicationRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ScheduleRepository scheduleRepository;

    @Test
    public void chaosCreate() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        when(this.applicationRepository.getOne(1L))
            .thenReturn(application);

        when(this.scheduleRepository.getOne(2L))
            .thenReturn(schedule);

        when(this.chaosRepository.saveAndFlush(new Chaos(application, 0.1, schedule)))
            .then(invocation -> {
                Chaos chaos = invocation.getArgumentAt(0, Chaos.class);
                chaos.setId(3L);
                return chaos;
            });

        ConstrainedFields fields = new ConstrainedFields(ChaosCreateInput.class);

        this.mockMvc.perform(post("/chaoses").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("application", "https://chaos-loris/applications/1")
                .entry("probability", 0.1)
                .entry("schedule", "https://chaos-loris/schedules/2")
                .build())))
            .andDo(document(
                requestFields(
                    fields.withPath("application").description("The URI of the application to create chaos on"),
                    fields.withPath("probability").description("The probability of an instance of the application experiencing chaos"),
                    fields.withPath("schedule").description("The URI of the schedule to create chaos on")),
                responseHeaders(headerWithName("Location").description("The URI of the newly created chaos"))));
    }

    @Test
    public void chaosDelete() throws Exception {
        this.mockMvc.perform(delete("/chaoses/{id}", 1L))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The chaos' id"))));
    }

    @Test
    public void chaosList() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(1L);

        PageRequest pageable = new PageRequest(2, 3);

        when(this.chaosRepository.findAll(pageable))
            .thenReturn(page(pageable, item -> {
                Application application = new Application(UUID.randomUUID());
                application.setId(item + 10);

                Chaos chaos = new Chaos(application, 0.1, schedule);
                chaos.setId(item);
                return chaos;
            }));

        this.mockMvc.perform(get("/chaoses" + query(pageable)).accept(HAL_JSON))
            .andDo(document(
                listRequestParameters(),
                listResponseFields(fieldWithPath("_embedded.chaoses").description("A collection of Chaoses as described in [Read a Chaos](#read-a-chaos)")),
                listLinks()));
    }

    @Test
    public void chaosRead() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        Event event = new Event(chaos, Instant.now(), Collections.singletonList(3), 10);
        event.setId(4L);

        when(this.chaosRepository.getOne(chaos.getId()))
            .thenReturn(chaos);

        when(this.eventRepository.findByChaos(chaos))
            .thenReturn(Collections.singletonList(event));

        this.mockMvc.perform(get("/chaoses/{id}", chaos.getId()).accept(HAL_JSON))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The chaos' id")),
                responseFields(fieldWithPath("probability").description("The probability of an instance of the application experiencing chaos")),
                links(
                    linkWithRel("application").description("The [Application](#applications) to create chaos on"),
                    linkWithRel("event").description("The [Events](#events) performed by this chaos"),
                    linkWithRel("schedule").description("The [Schedule](#schedules) to create chaos on"))));
    }

    @Test
    public void chaosUpdate() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        when(this.chaosRepository.getOne(chaos.getId()))
            .thenReturn(chaos);

        ConstrainedFields fields = new ConstrainedFields(ChaosUpdateInput.class);

        this.mockMvc.perform(patch("/chaoses/{id}", chaos.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("probability", 0.5))))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The chaos' id")),
                requestFields(fields.withPath("probability").type(NUMBER).description("The probability of an instance of the application experiencing chaos").optional())));
    }

    @EnableSpringDataWebSupport
    @TestConfiguration
    static class AdditionalConfiguration implements RestDocsMockMvcConfigurationCustomizer {

        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer
                .uris().withScheme("https").withHost("chaos-loris").withPort(443).and()
                .snippets().withTemplateFormat(TemplateFormats.markdown());
        }

        @Bean
        ChaosResourceAssembler chaosResourceAssembler(EventRepository eventRepository) {
            return new ChaosResourceAssembler(eventRepository);
        }

    }

}
