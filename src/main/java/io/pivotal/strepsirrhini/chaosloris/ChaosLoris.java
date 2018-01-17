/*
 * Copyright 2015-2018 the original author or authors.
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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Main entry point and configuration class
 */
@SpringBootApplication
public class ChaosLoris {

    /**
     * Start method
     *
     * @param args command line argument
     * @throws Exception as part of specification
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ChaosLoris.class, args).start();
    }

    @Bean
    ReactorCloudFoundryClient cloudFoundryClient(@Value("${loris.cloudfoundry.host}") String host,
                                                 @Value("${loris.cloudfoundry.password}") String password,
                                                 @Value("${loris.cloudfoundry.skipSslValidation:false}") Boolean skipSslValidation,
                                                 @Value("${loris.cloudfoundry.username}") String username) {

        return ReactorCloudFoundryClient.builder()
            .connectionContext(DefaultConnectionContext.builder()
                .apiHost(host)
                .skipSslValidation(skipSslValidation)
                .build())
            .tokenProvider(PasswordGrantTokenProvider.builder()
                .password(password)
                .username(username)
                .build())
            .build();
    }

    @Bean
    JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    Random random() {
        return new SecureRandom();
    }

    @Bean(destroyMethod = "shutdown")
    ThreadPoolTaskScheduler taskScheduler(@Value("${loris.scheduler.size}") int poolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix("loris-scheduler-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        return taskScheduler;
    }

}
