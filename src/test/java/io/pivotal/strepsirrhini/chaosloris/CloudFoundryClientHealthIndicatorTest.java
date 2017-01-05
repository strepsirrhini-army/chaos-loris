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

package io.pivotal.strepsirrhini.chaosloris;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.client.v2.info.Info;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class CloudFoundryClientHealthIndicatorTest {

    private final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);

    private final CloudFoundryClientHealthIndicator healthIndicator = new CloudFoundryClientHealthIndicator(this.cloudFoundryClient);

    private final Info info = mock(Info.class);

    @Test
    public void healthy() throws Exception {
        when(this.cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build()))
            .thenReturn(Mono
                .just(GetInfoResponse.builder()
                    .apiVersion("2.62.0")
                    .build()));

        Health.Builder builder = new Health.Builder();

        this.healthIndicator.doHealthCheck(builder);

        Health health = builder.build();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("apiVersion", "2.62.0");
    }

    @Test(expected = IllegalStateException.class)
    public void notHealthy() throws Exception {
        when(this.cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build()))
            .thenReturn(Mono
                .error(new IllegalStateException("test-message")));

        Health.Builder builder = new Health.Builder();


        this.healthIndicator.doHealthCheck(builder);
    }

    @Before
    public void setUp() throws Exception {
        when(this.cloudFoundryClient.info()).thenReturn(this.info);
    }

}
