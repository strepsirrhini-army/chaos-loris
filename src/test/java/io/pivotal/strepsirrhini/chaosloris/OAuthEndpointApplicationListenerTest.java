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

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class OAuthEndpointApplicationListenerTest {

    private final MockEnvironment environment = new MockEnvironment();

    private final ApplicationEnvironmentPreparedEvent event = new ApplicationEnvironmentPreparedEvent(
            new SpringApplication(), new String[0], this.environment);

    private final RestTemplate restTemplate = mock(RestTemplate.class);

    private final OAuthEndpointApplicationListener applicationListener = new OAuthEndpointApplicationListener(
            this.restTemplate);

    @Before
    public void setUp() {
        this.environment.withProperty("ers.apiUri", "https://test-api-uri");

        Map<String, String> payload = new HashMap<>();
        payload.put("authorization_endpoint", "https://test-authorization-endpoint-uri");
        payload.put("token_endpoint", "https://test-token-endpoint-uri");

        when(this.restTemplate.getForObject("https://test-api-uri/info", Map.class)).thenReturn(payload);
    }

    @Test
    public void onApplicationEvent() {
        this.applicationListener.onApplicationEvent(this.event);

        assertEquals("https://test-token-endpoint-uri/oauth/token",
                this.environment.getProperty("security.oauth2.client.accessTokenUri"));
        assertEquals("https://test-authorization-endpoint-uri/oauth/authorize",
                this.environment.getProperty("security.oauth2.client.userAuthorizationUri"));
        assertEquals("https://test-token-endpoint-uri/userinfo",
                this.environment.getProperty("security.oauth2.resource.userInfoUri"));
    }

}
