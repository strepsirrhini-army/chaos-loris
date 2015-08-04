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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class AbstractSerializationTest<T> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);

    @Test
    public final void serialization() throws IOException {
        assertContents((Map) roundTrip(getInstance()));
    }

    protected final Object roundTrip(Object o) throws IOException {
        String s = this.objectMapper.writeValueAsString(o);

        if (o instanceof List) {
            return this.objectMapper.readValue(s, List.class);
        } else {
            return this.objectMapper.readValue(s, Map.class);
        }
    }

    protected abstract void assertContents(Map m) throws IOException;

    protected abstract T getInstance();
}
