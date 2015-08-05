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

package io.pivotal.strepsirrhini.chaosloris.servicebroker.binding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

final class BindingResponse {

    private final Credentials credentials;

    private final URI syslogDrainUrl;

    BindingResponse() {
        this(null, null);
    }

    BindingResponse(Credentials credentials) {
        this(credentials, null);
    }

    BindingResponse(URI syslogDrainUrl) {
        this(null, syslogDrainUrl);
    }

    BindingResponse(Credentials credentials, URI syslogDrainUrl) {
        this.credentials = credentials;
        this.syslogDrainUrl = syslogDrainUrl;
    }

    Credentials getCredentials() {
        return this.credentials;
    }

    @JsonProperty("syslog_drain_url")
    URI getSyslogDrainUrl() {
        return this.syslogDrainUrl;
    }

}
