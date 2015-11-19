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

package io.pivotal.strepsirrhini.chaosloris.web;

import io.pivotal.strepsirrhini.chaosloris.AbstractIntegrationTest;
import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChaosResourceAssemblerTest extends AbstractIntegrationTest {

    @Autowired
    private ChaosResourceAssembler resourceAssembler;

    @Test
    public void toResource() {
        Application application = new Application(UUID.randomUUID());
        application.setId(-1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-2L);

        Chaos chaos = new Chaos(application, 0.2, schedule);
        chaos.setId(-3L);

        ChaosResourceAssembler.ChaosResource resource = this.resourceAssembler.toResource(chaos);

        assertThat(resource.getContent()).isSameAs(chaos);
        assertThat(resource.getLinks()).hasSize(3);
        assertThat(resource.getLink("application")).isNotNull();
        assertThat(resource.getLink("schedule")).isNotNull();
    }
}
