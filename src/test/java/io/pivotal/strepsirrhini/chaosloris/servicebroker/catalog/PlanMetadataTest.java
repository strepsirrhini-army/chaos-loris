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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class PlanMetadataTest extends AbstractSerializationTest<PlanMetadata> {

    @Override
    protected void assertContents(Map m) {
        assertEquals(getBullets(), m.get("bullets"));
        assertNull(m.get("costs"));
        assertEquals("test-display-name", m.get("displayName"));
    }

    @Override
    protected PlanMetadata getInstance() {
        return new PlanMetadata(null)
                .bullets("test-bullet-1", "test-bullet-2")
                .displayName("test-display-name");
    }

    private List<String> getBullets() {
        return Arrays.asList("test-bullet-1", "test-bullet-2");
    }

}
