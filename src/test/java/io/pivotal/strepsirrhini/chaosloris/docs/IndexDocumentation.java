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

import io.pivotal.strepsirrhini.chaosloris.web.IndexController;
import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.document;
import static io.pivotal.strepsirrhini.chaosloris.docs.DocumentationUtilities.links;
import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_REQUEST_URI;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriScheme = "https", uriHost = "chaos-loris", uriPort = 443)
@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexDocumentation {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void error() throws Exception {
        this.mockMvc
            .perform(get("/error")
                .requestAttr(ERROR_STATUS_CODE, 400)
                .requestAttr(ERROR_REQUEST_URI, "/schedules")
                .requestAttr(ERROR_MESSAGE, "The schedule 'https://chaos-loris/schedules/123' does not exist"))
            .andDo(document(
                responseFields(
                    fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                    fieldWithPath("message").description("A description of the cause of the error"),
                    fieldWithPath("path").description("The path to which the request was made"),
                    fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                    fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
    }

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/").accept(HAL_JSON))
            .andDo(document(
                links(
                    linkWithRel("applications").description("Link to operations on [Applications](#applications)"),
                    linkWithRel("chaoses").description("Link to operations on [Chaos](#chaoses)"),
                    linkWithRel("events").description("Link to operations on [Events](#events)"),
                    linkWithRel("schedules").description("Link to operations on [Schedules](#schedules)"))));
    }

    @ImportAutoConfiguration(ErrorMvcAutoConfiguration.class)
    @TestConfiguration
    static class AdditionalConfiguration implements RestDocsMockMvcConfigurationCustomizer {

        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.snippets().withTemplateFormat(TemplateFormats.markdown());
        }

    }

}
