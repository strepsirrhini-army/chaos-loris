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

import io.pivotal.strepsirrhini.chaosloris.servicebroker.AbstractSerializationTest;

import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class DashboardClientTest extends AbstractSerializationTest<DashboardClient> {

    @Override
    protected void assertContents(Map m) {
        assertEquals("test-id", m.get("id"));
        assertEquals("test-secret", m.get("secret"));
        assertEquals("https://test.redirect.uri", m.get("redirect_uri"));
    }

    @Override
    protected DashboardClient getInstance() {
        return new DashboardClient(null)
                .id("test-id")
                .secret("test-secret")
                .redirectUri(URI.create("https://test.redirect.uri"));
    }

}
