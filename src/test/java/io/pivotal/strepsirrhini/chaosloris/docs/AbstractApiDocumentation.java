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

import io.pivotal.strepsirrhini.chaosloris.web.AbstractControllerTest;
import org.junit.Rule;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

import java.util.function.Consumer;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public abstract class AbstractApiDocumentation extends AbstractControllerTest {

    protected static final Snippet PAGEABLE_LINKS = links(
            linkWithRel("first").optional().description("The first page of results"),
            linkWithRel("last").optional().description("The last page of results"),
            linkWithRel("next").optional().description("The next page of results"),
            linkWithRel("prev").optional().description("The previous page of results"));

    protected static final Snippet PAGEABLE_REQUEST_PARAMETERS = requestParameters(
            parameterWithName("page").description("Page to retrieve"),
            parameterWithName("size").description("Size of the page to retrieve"),
            parameterWithName("sort").description("Properties that should be sorted by in the format `property,property(,ASC|DESC)`. Default sort direction is ascending. Use multiple `sort` parameters to switch directions, e.g. `?sort=firstname&sort=lastname,asc`."));

    protected static final Snippet PAGEABLE_RESPONSE_FIELDS = responseFields(
            fieldWithPath("page.number").description("The number of this page of results"),
            fieldWithPath("page.size").description("The size of this page of results"),
            fieldWithPath("page.totalPages").description("The total number of pages of results"),
            fieldWithPath("page.totalElements").description("The total number of results"));

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");

    protected RestDocumentationResultHandler document;

    @Override
    protected final void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        this.document = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        mockMvcBuilder
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris().withScheme("https").withHost("chaos-lemur").withPort(443).and()
                        .writerResolver(new MarkdownWriterResolver()))
                .alwaysDo(this.document);
    }

    protected final String createPages(Consumer<Integer> pageCreator) {
        for (int i = 0; i < 12; i++) {
            pageCreator.accept(i);
        }

        return "?page=2&size=3";
    }

}
