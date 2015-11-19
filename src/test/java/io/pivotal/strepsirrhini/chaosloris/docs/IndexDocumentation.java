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

import org.junit.Test;

import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_REQUEST_URI;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class IndexDocumentation extends AbstractApiDocumentation {

    @Test
    public void error() throws Exception {
        this.document.snippets(
                responseFields(
                        fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                        fieldWithPath("message").description("A description of the cause of the error"),
                        fieldWithPath("path").description("The path to which the request was made"),
                        fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                        fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))
        );

        this.mockMvc
                .perform(get("/error")
                        .requestAttr(ERROR_STATUS_CODE, 400)
                        .requestAttr(ERROR_REQUEST_URI, "/schedules")
                        .requestAttr(ERROR_MESSAGE, "The schedule 'https://chaos-lemur/schedules/123' does not exist"));
    }

    @Test
    public void index() throws Exception {
        this.document.snippets(
                links(
                        linkWithRel("self").ignored(),
                        linkWithRel("applications").description("Link to operations on [Applications](#applications)"),
                        linkWithRel("chaoses").description("Link to operations on [Chaos](#chaoses)"),
                        linkWithRel("schedules").description("Link to operations on [Schedules](#schedules)")));

        this.mockMvc.perform(get("/").accept(HAL_JSON));
    }

}
