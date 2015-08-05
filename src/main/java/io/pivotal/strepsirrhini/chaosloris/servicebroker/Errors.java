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

package io.pivotal.strepsirrhini.chaosloris.servicebroker;

import com.fasterxml.jackson.annotation.JsonFormat;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT;

/**
 * Errors returned by the Service Broker API
 */
@JsonFormat(shape = OBJECT)
public enum Errors {
    REQUIRES_APPLICATION("RequiresApp",
            "This service supports generation of credentials through binding an application only.");

    private final String error;

    private final String description;

    Errors(String error, String description) {
        this.error = error;
        this.description = description;
    }

    /**
     * Returns the error field for the error
     *
     * @return the error field for the error
     */
    public String getError() {
        return this.error;
    }

    /**
     * Returns the description field for the error
     *
     * @return the description field for the error
     */
    public String getDescription() {
        return this.description;
    }
}
