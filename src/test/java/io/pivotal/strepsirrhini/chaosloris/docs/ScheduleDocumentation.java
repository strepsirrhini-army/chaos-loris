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
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleController;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleCreateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleResourceAssembler;
import io.pivotal.strepsirrhini.chaosloris.web.ScheduleUpdateInput;
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

import java.util.Arrays;
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
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriScheme = "https", uriHost = "chaos-loris", uriPort = 443)
@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleDocumentation {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ScheduleRepository scheduleRepository;

    @Test
    public void scheduleCreate() throws Exception {
        when(this.scheduleRepository.saveAndFlush(new Schedule("0 0 * * * *", "hourly")))
            .then(invocation -> {
                Schedule schedule = invocation.getArgumentAt(0, Schedule.class);
                schedule.setId(1L);
                return schedule;
            });

        ConstrainedFields fields = new ConstrainedFields(ScheduleCreateInput.class);

        this.mockMvc.perform(post("/schedules").contentType(APPLICATION_JSON)
            .content(asJson(FluentMap.builder()
                .entry("expression", "0 0 * * * *")
                .entry("name", "hourly")
                .build())))
            .andDo(document(
                requestFields(
                    fields.withPath("expression").description("The [CRON expression](http://docs.spring" +
                        ".io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) of the schedule"),
                    fields.withPath("name").description("The user-readable name of the schedule")),
                responseHeaders(headerWithName("Location").description("The URI of the newly created schedule"))));
    }

    @Test
    public void scheduleDelete() throws Exception {
        this.mockMvc.perform(delete("/schedules/{id}", 1L))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The schedule's id"))));
    }

    @Test
    public void scheduleList() throws Exception {
        PageRequest pageable = new PageRequest(2, 3);

        when(this.scheduleRepository.findAll(pageable))
            .thenReturn(page(pageable, item -> {
                Schedule schedule = new Schedule("0 0 * * * *", "hourly-" + item);
                schedule.setId(item);
                return schedule;
            }));

        this.mockMvc.perform(get("/schedules" + query(pageable)).accept(HAL_JSON))
            .andDo(document(
                listRequestParameters(),
                listResponseFields(fieldWithPath("_embedded.schedules").description("A collection of Schedules as described in [Read a Schedule](#read-a-schedule)")),
                listLinks()));
    }

    @Test
    public void scheduleRead() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(1L);

        Application application1 = new Application(UUID.randomUUID());
        application1.setId(2L);

        Chaos chaos1 = new Chaos(application1, 0.1, schedule);
        chaos1.setId(3L);

        Application application2 = new Application(UUID.randomUUID());
        application2.setId(4L);

        Chaos chaos2 = new Chaos(application2, 0.1, schedule);
        chaos2.setId(5L);

        when(this.scheduleRepository.getOne(schedule.getId()))
            .thenReturn(schedule);

        when(this.chaosRepository.findBySchedule(schedule))
            .thenReturn(Arrays.asList(chaos1, chaos2));

        this.mockMvc.perform(get("/schedules/{id}", schedule.getId()).accept(HAL_JSON))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The schedule's id")),
                responseFields(
                    fieldWithPath("expression").description("The [CRON expression](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator" +
                        ".html) of the schedule"),
                    fieldWithPath("name").description("The user-readable name of the schedule")),
                links(linkWithRel("chaos").description("[Chaos](#chaoses) instances using this schedule"))));
    }

    @Test
    public void scheduleUpdate() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(1L);

        when(this.scheduleRepository.getOne(schedule.getId()))
            .thenReturn(schedule);

        ConstrainedFields fields = new ConstrainedFields(ScheduleUpdateInput.class);

        this.mockMvc.perform(patch("/schedules/{id}", schedule.getId()).contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("name", "default"))))
            .andDo(document(
                pathParameters(
                    parameterWithName("id").description("The schedule's id")), requestFields(
                    fields.withPath("expression").type(STRING).description("The [CRON expression](http://docs.spring" +
                        ".io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) of the schedule").optional(),
                    fields.withPath("name").type(STRING).description("The user-readable name of the schedule").optional())));
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
        ScheduleResourceAssembler scheduleResourceAssembler(ChaosRepository chaosRepository) {
            return new ScheduleResourceAssembler(chaosRepository);
        }

    }

}
