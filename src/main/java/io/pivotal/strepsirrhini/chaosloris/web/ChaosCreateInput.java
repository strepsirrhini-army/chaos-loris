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

package io.pivotal.strepsirrhini.chaosloris.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Input for chaos creation
 */
public final class ChaosCreateInput {

    @NotNull
    private final URI application;

    @DecimalMax("1")
    @DecimalMin("0")
    @NotNull
    private final Double probability;

    @NotNull
    private final URI schedule;

    @JsonCreator
    ChaosCreateInput(@JsonProperty("application") URI application, @JsonProperty("probability") Double probability, @JsonProperty("schedule") URI schedule) {
        this.application = application;
        this.probability = probability;
        this.schedule = schedule;
    }

    URI getApplication() {
        return this.application;
    }

    Double getProbability() {
        return this.probability;
    }

    URI getSchedule() {
        return this.schedule;
    }

}
