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

package io.pivotal.strepsirrhini.chaosloris.destroyer;

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
final class CloudFoundryPlatform implements Platform {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    protected CloudFoundryPlatform(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Integer> getInstanceCount(Application application) {
        return getInstances(this.cloudFoundryClient, application.getApplicationId().toString())
            .doOnSuccess(instanceCount -> this.logger.debug("{} has {} instances", application, instanceCount));
    }

    @Override
    public Mono<Void> terminateInstance(Application application, Integer index) {
        return requestTerminateInstance(this.cloudFoundryClient, application.getApplicationId().toString(), index.toString())
            .doOnSubscribe(s -> this.logger.info("Terminate {}/{}", application, index))
            .doOnSuccess(v -> this.logger.debug("Terminated {}/{}", application, index));
    }

    private static Mono<SummaryApplicationResponse> requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<Void> requestTerminateInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String index) {
        return cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(index)
                .build());
    }

    private Mono<Integer> getInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationSummary(cloudFoundryClient, applicationId)
            .map(SummaryApplicationResponse::getInstances);
    }

}
