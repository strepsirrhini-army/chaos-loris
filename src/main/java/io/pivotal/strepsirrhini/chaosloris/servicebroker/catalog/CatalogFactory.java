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

package io.pivotal.strepsirrhini.chaosloris.servicebroker.catalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Configuration
public class CatalogFactory {

    private static final UUID PLAN_ID = UUID.fromString("87E67009-218A-436E-ACBC-A387F7E08C24");

    private static final UUID SERVICE_ID = UUID.fromString("0120774A-50D9-4F26-8165-302DD4778691");

    @Bean
    Catalog catalog(@Value("${serviceBroker.dashboardSecret}") String dashboardSecret,
                    @Value("${serviceBroker.host}") String host) {
        // @formatter:off
        return new Catalog()
            .service()
                .id(SERVICE_ID)
                .name("chaos-loris")
                .description("An adaptation of the Chaos Monkey concept to Cloud Foundry application instances")
                .bindable(true)
                .tags("chaos-loris")
                .metadata()
                    .displayName("Chaos Loris")
//                    .imageUrl(imageUri(host))
                    .longDescription("An adaptation of the Chaos Monkey concept to Cloud Foundry application instances")
                    .providerDisplayName("Pivotal Software, Inc.")
//                    .documentationUrl(URI.create("https://docs.pivotal.io"))
//                    .supportUrl(URI.create("https://support.pivotal.io"))
                    .and()
                .planUpdateable(true)
                .plan()
                    .id(PLAN_ID)
                    .name("default")
                    .description("An adaptation of the Chaos Monkey concept to Cloud Foundry application instances")
                    .metadata()
                        .displayName("An adaptation of the Chaos Monkey concept to Cloud Foundry application instances")
                        .and()
                    .free(true)
                    .and()
                .dashboardClient()
                    .id("chaos-loris-dashboard")
                    .secret(dashboardSecret)
                    .redirectUri(redirectUri(host))
                    .and()
                .and()
        ;
        // @formatter:on
    }

    private URI imageUri(String host) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host)
                .pathSegment("images")
                .pathSegment("p-spring-xd@2x.png")
                .build().toUri();
    }

    private URI redirectUri(String host) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host)
                .build().toUri();
    }

}
