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

import java.net.URI;

final class ServiceMetadata {

    private final Service service;

    private final Object monitor = new Object();

    private volatile String displayName;

    private volatile URI imageUrl;

    private volatile String longDescription;

    private volatile String providerDisplayName;

    private volatile URI documentationUrl;

    private volatile URI supportUrl;

    ServiceMetadata(Service service) {
        this.service = service;
    }

    String getDisplayName() {
        synchronized (this.monitor) {
            return this.displayName;
        }
    }

    URI getImageUrl() {
        synchronized (this.monitor) {
            return this.imageUrl;
        }
    }

    String getLongDescription() {
        synchronized (this.monitor) {
            return this.longDescription;
        }
    }

    String getProviderDisplayName() {
        synchronized (this.monitor) {
            return this.providerDisplayName;
        }
    }

    URI getDocumentationUrl() {
        synchronized (this.monitor) {
            return this.documentationUrl;
        }
    }

    URI getSupportUrl() {
        synchronized (this.monitor) {
            return this.supportUrl;
        }
    }

    Service and() {
        synchronized (this.monitor) {
            return this.service;
        }
    }

    ServiceMetadata displayName(String displayName) {
        synchronized (this.monitor) {
            this.displayName = displayName;
            return this;
        }
    }

    ServiceMetadata imageUrl(URI imageUrl) {
        synchronized (this.monitor) {
            this.imageUrl = imageUrl;
            return this;
        }
    }

    ServiceMetadata longDescription(String longDescription) {
        synchronized (this.monitor) {
            this.longDescription = longDescription;
            return this;
        }
    }

    ServiceMetadata providerDisplayName(String providerDisplayName) {
        synchronized (this.monitor) {
            this.providerDisplayName = providerDisplayName;
            return this;
        }
    }

    ServiceMetadata documentationUrl(URI documentationUrl) {
        synchronized (this.monitor) {
            this.documentationUrl = documentationUrl;
            return this;
        }
    }

    ServiceMetadata supportUrl(URI supportUrl) {
        synchronized (this.monitor) {
            this.supportUrl = supportUrl;
            return this;
        }
    }

}
