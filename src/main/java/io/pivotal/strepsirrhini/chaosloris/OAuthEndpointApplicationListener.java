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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

final class OAuthEndpointApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String KEY_API_URI = "ers.apiUri";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;

    OAuthEndpointApplicationListener() {
        this(new RestTemplate());
    }

    OAuthEndpointApplicationListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        this.logger.info("Retrieving OAuth endpoints");

        ConfigurableEnvironment environment = event.getEnvironment();

        Map<String, Object> endpoints = new HashMap<>();

        Map payload = this.restTemplate.getForObject(getApiUri(environment), Map.class);
        endpoints.put("security.oauth2.client.accessTokenUri", getAccessTokenUri(payload));
        endpoints.put("security.oauth2.client.userAuthorizationUri", getUserAuthorizationUri(payload));
        endpoints.put("security.oauth2.resource.userInfoUri", getUserInfoUri(payload));

        environment.getPropertySources().addFirst(new MapPropertySource("OAuthEndpoints", endpoints));
    }

    private String getApiUri(PropertyResolver propertyResolver) {
        String apiUri = propertyResolver.getRequiredProperty(KEY_API_URI);

        return UriComponentsBuilder.fromUriString(apiUri).pathSegment("info").build().toUriString();
    }

    private String getUserAuthorizationUri(Map payload) {
        String authorizationEndpoint = (String) payload.get("authorization_endpoint");
        Assert.notNull(authorizationEndpoint, "authorization_endpoint must be specified");

        return UriComponentsBuilder.fromUriString(authorizationEndpoint)
                .pathSegment("oauth", "authorize").build().toUriString();
    }

    private String getAccessTokenUri(Map payload) {
        return getTokenEndpoint(payload).pathSegment("oauth", "token").build().toUriString();
    }

    private String getUserInfoUri(Map payload) {
        return getTokenEndpoint(payload).pathSegment("userinfo").build().toUriString();
    }

    private UriComponentsBuilder getTokenEndpoint(Map payload) {
        String tokenEndpoint = (String) payload.get("token_endpoint");
        Assert.notNull(tokenEndpoint, "token_endpoint must be specified");

        return UriComponentsBuilder.fromUriString(tokenEndpoint);
    }

}
