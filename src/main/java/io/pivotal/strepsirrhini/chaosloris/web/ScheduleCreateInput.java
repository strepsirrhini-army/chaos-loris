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

import javax.validation.constraints.NotNull;

/**
 * Input for schedule creation
 */
public final class ScheduleCreateInput {

    @NotNull
    private final String expression;

    @NotNull
    private final String name;

    @JsonCreator
    ScheduleCreateInput(@JsonProperty("expression") String expression, @JsonProperty("name") String name) {
        this.expression = expression;
        this.name = name;
    }

    String getExpression() {
        return this.expression;
    }

    String getName() {
        return this.name;
    }

}
