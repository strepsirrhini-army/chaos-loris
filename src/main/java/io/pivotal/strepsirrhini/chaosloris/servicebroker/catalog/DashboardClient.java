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

import java.net.URI;

final class DashboardClient {

    private final Service service;

    private final Object monitor = new Object();

    private volatile String id;

    private volatile String secret;

    private volatile URI redirectUri;

    DashboardClient(Service service) {
        this.service = service;
    }

    String getId() {
        synchronized (this.monitor) {
            Assert.notNull(this.id, "Dashboard Clients must specify an id");
            return this.id;
        }
    }

    String getSecret() {
        synchronized (this.monitor) {
            Assert.notNull(this.secret, "Dashboard Clients must specify a secret");
            return this.secret;
        }
    }

    @JsonProperty("redirect_uri")
    URI getRedirectUri() {
        synchronized (this.monitor) {
            Assert.notNull(this.redirectUri, "Dashboard Clients must specify a redirect URI");
            return this.redirectUri;
        }
    }

    Service and() {
        synchronized (this.monitor) {
            return this.service;
        }
    }

    DashboardClient id(String id) {
        synchronized (this.monitor) {
            this.id = id;
            return this;
        }
    }

    DashboardClient secret(String secret) {
        synchronized (this.monitor) {
            this.secret = secret;
            return this;
        }
    }

    DashboardClient redirectUri(URI redirectUri) {
        synchronized (this.monitor) {
            this.redirectUri = redirectUri;
            return this;
        }
    }

}
