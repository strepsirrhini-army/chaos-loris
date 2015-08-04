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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractDeserializationTest<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Class<T> type;

    protected AbstractDeserializationTest(Class<T> type) {
        this.type = type;
    }

    @Test
    public final void deSerialization() throws IOException {
        assertContents(roundTrip(getMap()));
    }

    protected final T roundTrip(Map m) throws IOException {
        String s = this.objectMapper.writeValueAsString(m);
        return this.objectMapper.readValue(s, this.type);
    }

    protected abstract void assertContents(T instance);

    protected abstract Map getMap();
}
