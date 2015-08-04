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

package io.pivotal.strepsirrhini.chaosloris;

import com.github.zafarkhaja.semver.Version;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

final class BrokerApiVersionFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Broker-Api-Version";

    private static final String VALID_VERSIONS = "^2.3";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String header = request.getHeader(HEADER);
        if (header == null) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        Integer[] parsed = parse(header);
        Version version = Version.forIntegers(parsed[0], parsed[1]);
        if (!version.satisfies(VALID_VERSIONS)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Integer[] parse(String header) {
        return Stream.of(header.split("\\.")).map(Integer::parseInt).toArray(Integer[]::new);
    }

}
