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

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class PlanTest extends AbstractSerializationTest<Plan> {

    @Override
    protected void assertContents(Map m) throws IOException {
        assertEquals(getId().toString(), m.get("id"));
        assertEquals("test-name", m.get("name"));
        assertEquals("test-description", m.get("description"));
        assertNull(m.get("metadata"));
        assertTrue((Boolean) m.get("free"));
    }

    @Override
    protected Plan getInstance() {
        return new Plan(null)
                .id(getId())
                .name("test-name")
                .description("test-description")
                .free(true);
    }

    public UUID getId() {
        return UUID.nameUUIDFromBytes(new byte[0]);
    }

}
