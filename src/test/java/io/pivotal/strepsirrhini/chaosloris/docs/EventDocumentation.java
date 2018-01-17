/*
 * Copyright 2015-2018 the original author or authors.
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
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.web.EventController;
import io.pivotal.strepsirrhini.chaosloris.web.EventResourceAssembler;
import org.cloudfoundry.client.CloudFoundryClient;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriScheme = "https", uriHost = "chaos-loris", uriPort = 443)
@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventDocumentation {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void eventDelete() throws Exception {
        this.mockMvc.perform(delete("/events/{id}", 1L))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The event's id"))));
    }

    @Test
    public void eventList() throws Exception {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(1L);

        Application application = new Application(UUID.randomUUID());
        application.setId(2L);

        Chaos chaos = new Chaos(application, 0.1, schedule);
        chaos.setId(3L);

        PageRequest pageable = new PageRequest(2, 3);

        when(this.eventRepository.findAll(pageable))
            .thenReturn(page(pageable, item -> {
                Event event = new Event(chaos, Instant.now(), Collections.singletonList((int) item % 10), 10);
                event.setId(item);
                return event;
            }));

        this.mockMvc.perform(get("/events" + query(pageable)).accept(HAL_JSON))
            .andDo(document(
                listRequestParameters(),
                listResponseFields(fieldWithPath("_embedded.events").description("A collection of Events as described in [Read an Event](#read-an-event)")),
                listLinks()));
    }

    @Test
    public void eventRead() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(-1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-2L);

        Chaos chaos = new Chaos(application, 0.2, schedule);
        chaos.setId(-3L);

        Event event = new Event(chaos, Instant.EPOCH, Arrays.asList(0, 1), Integer.MIN_VALUE);
        event.setId(-4L);

        when(this.eventRepository.getOne(schedule.getId()))
            .thenReturn(event);

        this.mockMvc.perform(get("/events/{id}", schedule.getId()).accept(HAL_JSON))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The event's id")),
                responseFields(
                    fieldWithPath("executedAt").description("An ISO-8601 timestamp for when the event occurred"),
                    fieldWithPath("terminatedInstances").description("The instances terminated during the event"),
                    fieldWithPath("totalInstanceCount").description("The total number of instances that were candidates for termination during the event"),
                    fieldWithPath("terminatedInstanceCount").description("The total number of instances terminated during the event")),
                links(linkWithRel("chaos").description("The [Chaos](#chaoses) that triggered the event"))));
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
        EventResourceAssembler eventResourceAssembler() {
            return new EventResourceAssembler();
        }

    }

}
