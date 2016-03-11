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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

/**
 * Main entry point and configuration class
 */
@SpringBootApplication
public class ChaosLoris {

    /**
     * Start method
     *
     * @param args command line argument
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ChaosLoris.class, args).start();
    }

    // TODO: Remove once Converters are configured without ConversionService
    @Bean
    public ConversionServiceFactoryBean conversionService(Set<Converter> converters) {
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        factoryBean.setConverters(converters);

        return factoryBean;
    }

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${loris.cloudfoundry.host}") String host,
                                                @Value("${loris.cloudfoundry.username}") String username,
                                                @Value("${loris.cloudfoundry.port:443}") Integer port,
                                                @Value("${loris.cloudfoundry.password}") String password,
                                                @Value("${loris.cloudfoundry.skipSslValidation:false}") Boolean skipSslValidation) {

        return SpringCloudFoundryClient.builder()
            .host(host)
            .username(username)
            .port(port)
            .password(password)
            .skipSslValidation(skipSslValidation)
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
