/*
 * Copyright 2015-2018 the original author or authors.
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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Event;
import io.pivotal.strepsirrhini.chaosloris.data.EventRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardDestroyerTest {

    private final ChaosRepository chaosRepository = mock(ChaosRepository.class, RETURNS_SMART_NULLS);

    private final EventRepository eventRepository = mock(EventRepository.class, RETURNS_SMART_NULLS);

    private final FateEngine fateEngine = mock(FateEngine.class, RETURNS_SMART_NULLS);

    private final Platform platform = mock(Platform.class, RETURNS_SMART_NULLS);

    private final StandardDestroyer destroyer = new StandardDestroyer(this.chaosRepository, this.eventRepository, this.fateEngine, this.platform, Long.MIN_VALUE);

    @Test
    public void run() throws Exception {
        UUID applicationId = UUID.randomUUID();
        Application application = new Application(applicationId);
        Schedule schedule = new Schedule("test-expression", "test-name");
        Chaos chaos = new Chaos(application, Double.MIN_VALUE, schedule);

        when(this.chaosRepository.findByScheduleId(Long.MIN_VALUE)).thenReturn(Collections.singletonList(chaos));
        when(this.platform.getInstanceCount(application)).thenReturn(Mono.just(2));
        when(this.fateEngine.getFate(chaos)).thenReturn(FateEngine.Fate.THUMBS_UP, FateEngine.Fate.THUMBS_DOWN);
        when(this.platform.terminateInstance(application, 1)).thenReturn(Mono.empty());

        when(this.eventRepository.save(argThat(new ArgumentMatcher<Event>() {

            @Override
            public boolean matches(Object argument) {
                Event event = (Event) argument;
                return event.getChaos().equals(chaos) &&
                    event.getTerminatedInstances().equals(Collections.singletonList(1)) &&
                    event.getTerminatedInstanceCount() == 1;
            }

        }))).thenReturn(new Event(chaos, Instant.now(), Collections.singletonList(1), 1));

        this.destroyer.run();
    }

}
