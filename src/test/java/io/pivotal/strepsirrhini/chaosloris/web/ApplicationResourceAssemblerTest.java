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

import io.pivotal.strepsirrhini.chaosloris.data.Application;
import io.pivotal.strepsirrhini.chaosloris.data.Chaos;
import io.pivotal.strepsirrhini.chaosloris.data.ChaosRepository;
import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationResourceAssemblerTest {

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private ApplicationResourceAssembler resourceAssembler;

    @Test
    public void toResource() {
        Application application = new Application(UUID.randomUUID());
        application.setId(-1L);

        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-2L);

        Chaos chaos = new Chaos(application, 0.2, schedule);
        chaos.setId(-3L);

        when(this.chaosRepository.findByApplication(application))
            .thenReturn(Collections.singletonList(chaos));

        ApplicationResourceAssembler.ApplicationResource resource = this.resourceAssembler.toResource(application);

        assertThat(resource.getContent()).isSameAs(application);
        assertThat(resource.getLinks()).hasSize(2);
        assertThat(resource.getLink("chaos")).isNotNull();
    }

}
