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
import org.cloudfoundry.client.v2.applications.AbstractApplicationEntity;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.rx.Stream;
import reactor.rx.Streams;

@Component
final class CloudFoundryPlatform implements Platform {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    protected CloudFoundryPlatform(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Stream<Integer> getInstanceCount(Application application) {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id(application.getApplicationId().toString())
                .build();

        return Streams.wrap(this.cloudFoundryClient.applicationsV2().summary(request))
                .map(AbstractApplicationEntity::getInstances)
                .observe(instanceCount -> this.logger.debug("{} Instance Count: {}", application, instanceCount));
    }

    @Override
    public void terminateInstance(Application application, Integer instance) {
        this.logger.info("Terminating {}/{}", application, instance);
    }

}
