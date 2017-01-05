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
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.web.ApplicationController;
import io.pivotal.strepsirrhini.chaosloris.web.ApplicationCreateInput;
import io.pivotal.strepsirrhini.chaosloris.web.ApplicationResourceAssembler;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static io.pivotal.strepsirrhini.chaosloris.JsonTestUtilities.asJson;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.page;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.document;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.links;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listLinks;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listRequestParameters;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.listResponseFields;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriScheme = "https", uriHost = "chaos-loris", uriPort = 443)
@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationDocumentation {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ApplicationRepository applicationRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void applicationCreate() throws Exception {
        UUID applicationId = UUID.randomUUID();

        when(this.applicationRepository.saveAndFlush(new Application(applicationId)))
            .then(invocation -> {
                Application application = invocation.getArgumentAt(0, Application.class);
                application.setId(1L);
                return application;
            });

        ConstrainedFields fields = new ConstrainedFields(ApplicationCreateInput.class);

        this.mockMvc.perform(post("/applications").contentType(APPLICATION_JSON)
            .content(asJson(Collections.singletonMap("applicationId", applicationId.toString()))))
            .andDo(document(
                requestFields(fields.withPath("applicationId").description("The Cloud Foundry application id")),
                responseHeaders(headerWithName("Location").description("The URI of the newly created application"))));
    }

    @Test
    public void applicationDelete() throws Exception {
        this.mockMvc.perform(delete("/applications/{id}", 1L))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The application's id"))));
    }

    @Test
    public void applicationList() throws Exception {
        PageRequest pageable = new PageRequest(2, 3);

        when(this.applicationRepository.findAll(pageable))
            .thenReturn(page(pageable, item -> {
                Application application = new Application(UUID.randomUUID());
                application.setId(item);
                return application;
            }));

        this.mockMvc.perform(get("/applications" + query(pageable)).accept(HAL_JSON))
            .andDo(document(
                listRequestParameters(),
                listResponseFields(fieldWithPath("_embedded.applications").description("A collection of Applications as described in [Read an Application](#read-an-application)")),
                listLinks()));
    }

    @Test
    public void applicationRead() throws Exception {
        Application application = new Application(UUID.randomUUID());
        application.setId(1L);

        Schedule schedule1 = new Schedule("0 0 * * * *", "hourly");
        schedule1.setId(2L);

        Chaos chaos1 = new Chaos(application, 0.1, schedule1);
        chaos1.setId(3L);

        Schedule schedule2 = new Schedule("0 0 0 * * *", "daily");
        schedule2.setId(4L);

        Chaos chaos2 = new Chaos(application, 0.1, schedule2);
        chaos2.setId(5L);

        when(this.applicationRepository.getOne(application.getId()))
            .thenReturn(application);

        when(this.chaosRepository.findByApplication(application))
            .thenReturn(Arrays.asList(chaos1, chaos2));

        this.mockMvc.perform(get("/applications/{id}", application.getId()).accept(HAL_JSON))
            .andDo(document(
                pathParameters(parameterWithName("id").description("The application's id")),
                responseFields(fieldWithPath("applicationId").description("The Cloud Foundry application id")),
                links(linkWithRel("chaos").description("[Chaos](#chaoses) instances using this application"))));
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
        ApplicationResourceAssembler applicationResourceAssembler(ChaosRepository chaosRepository) {
            return new ApplicationResourceAssembler(chaosRepository);
        }

    }

}
