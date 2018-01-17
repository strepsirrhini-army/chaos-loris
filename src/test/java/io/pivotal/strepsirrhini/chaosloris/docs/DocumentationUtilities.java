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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;

import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

final class DocumentationUtilities {

    static RestDocumentationResultHandler document(Snippet... snippets) {
        return MockMvcRestDocumentation.document("{method-name}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            snippets);
    }

    static LinksSnippet links(LinkDescriptor... descriptors) {
        return HypermediaDocumentation.links(
            linkWithRel("self").ignored())
            .and(descriptors);
    }

    static LinksSnippet listLinks() {
        return HypermediaDocumentation.links(
            linkWithRel("first").description("The first page of results").optional(),
            linkWithRel("last").description("The last page of results").optional(),
            linkWithRel("next").description("The next page of results").optional(),
            linkWithRel("prev").description("The previous page of results").optional(),
            linkWithRel("self").ignored().optional());
    }

    static RequestParametersSnippet listRequestParameters() {
        return RequestDocumentation.requestParameters(
            parameterWithName("page").description("Page to retrieve").optional(),
            parameterWithName("size").description("Size of the page to retrieve").optional());
    }

    static ResponseFieldsSnippet listResponseFields(FieldDescriptor descriptor) {
        return PayloadDocumentation.responseFields(
            fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("The number of this page of results"),
            fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("The size of this page of results"),
            fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("The total number of pages of results"),
            fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("The total number of results"),
            fieldWithPath("_links").ignored())
            .and(descriptor);
    }

    static <T> Page<T> page(Pageable pageable, LongFunction<T> function) {
        return new PageImpl<>(LongStream.range(pageable.getOffset(), pageable.getOffset() + pageable.getPageSize())
            .mapToObj(function)
            .collect(Collectors.toList()), pageable, 12);
    }

    static String query(Pageable pageable) {
        return String.format("?page=%d&size=%d", pageable.getPageNumber(), pageable.getPageSize());
    }

    static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
        return PayloadDocumentation.responseFields(
            fieldWithPath("_links").ignored())
            .and(descriptors);
    }

}
