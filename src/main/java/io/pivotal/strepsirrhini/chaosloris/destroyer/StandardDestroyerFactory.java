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

package io.pivotal.strepsirrhini.chaosloris.destroyer;

import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final class StandardDestroyerFactory implements DestroyerFactory {

    private final ChaosRepository chaosRepository;

    private final EventRepository eventRepository;

    private final FateEngine fateEngine;

    private final Platform platform;

    @Autowired
    StandardDestroyerFactory(ChaosRepository chaosRepository, EventRepository eventRepository, FateEngine fateEngine, Platform platform) {
        this.chaosRepository = chaosRepository;
        this.eventRepository = eventRepository;
        this.fateEngine = fateEngine;
        this.platform = platform;
    }

    @Override
    public StandardDestroyer create(Long scheduleId) {
        return new StandardDestroyer(this.chaosRepository, this.eventRepository, this.fateEngine, this.platform, scheduleId);
    }

}
