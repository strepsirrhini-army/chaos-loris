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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class PlanMetadata {

    private final Plan plan;

    private final Object monitor = new Object();

    private volatile List<String> bullets;

    private volatile List<Cost> costs;

    private volatile String displayName;

    PlanMetadata(Plan plan) {
        this.plan = plan;
    }

    List<String> getBullets() {
        synchronized (this.monitor) {
            return this.bullets;
        }
    }

    List<Cost> getCosts() {
        synchronized (this.monitor) {
            return this.costs;
        }
    }

    String getDisplayName() {
        synchronized (this.monitor) {
            return this.displayName;
        }
    }

    Plan and() {
        synchronized (this.monitor) {
            return this.plan;
        }
    }

    PlanMetadata bullets(String... bullets) {
        synchronized (this.monitor) {
            if (this.bullets == null) {
                this.bullets = new ArrayList<>();
            }

            Arrays.stream(bullets).forEach(this.bullets::add);
            return this;
        }
    }

    Cost cost() {
        synchronized (this.monitor) {
            if (this.costs == null) {
                this.costs = new ArrayList<>();
            }

            Cost cost = new Cost(this);
            this.costs.add(cost);
            return cost;
        }
    }

    PlanMetadata displayName(String displayName) {
        synchronized (this.monitor) {
            this.displayName = displayName;
            return this;
        }
    }

}
