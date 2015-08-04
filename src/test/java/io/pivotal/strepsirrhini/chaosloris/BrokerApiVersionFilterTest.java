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

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class BrokerApiVersionFilterTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final MockFilterChain filterChain = new MockFilterChain();

    private final BrokerApiVersionFilter filter = new BrokerApiVersionFilter();

    @Test
    public void noHeader() throws IOException, ServletException {
        this.filter.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(HttpServletResponse.SC_PRECONDITION_FAILED, this.response.getStatus());
    }

    @Test
    public void tooEarly() throws IOException, ServletException {
        this.request.addHeader("X-Broker-Api-Version", 2.2);

        this.filter.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(HttpServletResponse.SC_PRECONDITION_FAILED, this.response.getStatus());
    }

    @Test
    public void tooLate() throws IOException, ServletException {
        this.request.addHeader("X-Broker-Api-Version", 3.0);

        this.filter.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(HttpServletResponse.SC_PRECONDITION_FAILED, this.response.getStatus());
    }

    @Test
    public void valid23() throws IOException, ServletException {
        this.request.addHeader("X-Broker-Api-Version", 2.3);

        this.filter.doFilterInternal(this.request, this.response, this.filterChain);

        assertNotNull(this.filterChain.getRequest());
    }

    @Test
    public void valid24() throws IOException, ServletException {
        this.request.addHeader("X-Broker-Api-Version", 2.4);

        this.filter.doFilterInternal(this.request, this.response, this.filterChain);

        assertNotNull(this.filterChain.getRequest());
    }

}
