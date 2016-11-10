/*
 * Copyright 2016 the original author or authors.
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

package io.pivotal.strepsirrhini.chaosloris.destroyer;

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class CloudFoundryPlatformTest {

    private final ApplicationsV2 applications = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);

    private final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);

    private final CloudFoundryPlatform platform = new CloudFoundryPlatform(this.cloudFoundryClient);

    @Test
    public void getInstanceCount() {
        UUID applicationId = UUID.randomUUID();

        requestApplicationSummary(this.cloudFoundryClient, applicationId.toString(), Mono.just(SummaryApplicationResponse.builder()
            .instances(1)
            .build()));

        this.platform.getInstanceCount(new Application(applicationId))
            .as(StepVerifier::create)
            .expectNext(1)
            .expectComplete()
            .verify();
    }

    @Before
    public void setUp() throws Exception {
        when(this.cloudFoundryClient.applicationsV2())
            .thenReturn(this.applications);
    }

    @Test
    public void terminateInstance() {
        UUID applicationId = UUID.randomUUID();

        requestTerminateInstance(this.cloudFoundryClient, applicationId.toString(), "0", Mono.empty());

        this.platform.terminateInstance(new Application(applicationId), 0)
            .as(StepVerifier::create)
            .expectComplete()
            .verify();

        verify(this.cloudFoundryClient.applicationsV2())
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId.toString())
                .index("0")
                .build());
    }

    private static void requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId, Mono<SummaryApplicationResponse> response) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(response);
    }

    private static void requestTerminateInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String index, Mono<Void> response) {
        when(cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(index)
                .build()))
            .thenReturn(response);
    }

}
