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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@Component
final class CloudFoundryClientHealthIndicator extends AbstractHealthIndicator {

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    CloudFoundryClientHealthIndicator(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // TODO: What's the best practice here

        Mono
            .when(
                requestGetInfo(this.cloudFoundryClient),
                Mono.just(builder)
            )
            .then(function((r, b) -> Mono.just(b.up())))
            .otherwise(t -> Mono.just(builder.down((Exception) t)))
            .get(Duration.ofSeconds(10));

//        requestGetInfo(this.cloudFoundryClient)
//            .doOnSuccess(response -> builder.up())
//            .doOnError(error -> builder.down((Exception) error))
//            .otherwise(error -> Mono.empty())
//            .get(Duration.ofSeconds(10));
    }

    private static Mono<GetInfoResponse> requestGetInfo(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build());
    }

}
