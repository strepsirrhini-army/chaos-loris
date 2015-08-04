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

package io.pivotal.strepsirrhini.chaosloris.servicebroker.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.UUID;

final class Plan {

    private final Service service;

    private final Object monitor = new Object();

    private volatile UUID id;

    private volatile String name;

    private volatile String description;

    private volatile PlanMetadata planMetadata;

    private volatile Boolean free;

    Plan(Service service) {
        this.service = service;
    }

    UUID getId() {
        synchronized (this.monitor) {
            Assert.notNull(this.id, "Plans must specify an id");
            return this.id;
        }
    }

    String getName() {
        synchronized (this.monitor) {
            Assert.notNull(this.id, "Plans must specify a name");
            return this.name;
        }
    }

    String getDescription() {
        synchronized (this.monitor) {
            Assert.notNull(this.id, "Plans must specify a description");
            return this.description;
        }
    }

    @JsonProperty("metadata")
    PlanMetadata getPlanMetadata() {
        synchronized (this.monitor) {
            return this.planMetadata;
        }
    }

    Boolean getFree() {
        synchronized (this.monitor) {
            return this.free;
        }
    }

    Service and() {
        synchronized (this.monitor) {
            return this.service;
        }
    }

    Plan id(UUID id) {
        synchronized (this.monitor) {
            this.id = id;
            return this;
        }
    }

    Plan name(String name) {
        synchronized (this.monitor) {
            this.name = name;
            return this;
        }
    }

    Plan description(String description) {
        synchronized (this.monitor) {
            this.description = description;
            return this;
        }
    }

    PlanMetadata metadata() {
        synchronized (this.monitor) {
            if (this.planMetadata == null) {
                this.planMetadata = new PlanMetadata(this);
            }

            return this.planMetadata;
        }
    }

    Plan free(Boolean free) {
        synchronized (this.monitor) {
            this.free = free;
            return this;
        }
    }

}
