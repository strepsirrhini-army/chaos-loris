/*
 * Copyright 2015-2017 the original author or authors.
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

package io.pivotal.strepsirrhini.chaosloris.data;

import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@RunWith(SpringRunner.class)
public class ChaosRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ChaosRepository chaosRepository;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    public void findByApplication() {
        Schedule schedule = new Schedule("test-schedule", "test-name");
        this.scheduleRepository.saveAndFlush(schedule);

        Application application1 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application1);

        Chaos chaos1 = new Chaos(application1, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos1);

        Application application2 = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application2);

        Chaos chaos2 = new Chaos(application2, 0.1, schedule);
        this.chaosRepository.saveAndFlush(chaos2);

        List<Chaos> chaoses = this.chaosRepository.findByApplication(application1);
        assertThat(chaoses).containsExactly(chaos1);
    }

    @Test
    public void findBySchedule() {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule1 = new Schedule("test-schedule-1", "test-name-1");
        this.scheduleRepository.saveAndFlush(schedule1);

        Chaos chaos1 = new Chaos(application, 0.1, schedule1);
        this.chaosRepository.saveAndFlush(chaos1);

        Schedule schedule2 = new Schedule("test-schedule-2", "test-name-2");
        this.scheduleRepository.saveAndFlush(schedule2);

        Chaos chaos2 = new Chaos(application, 0.1, schedule2);
        this.chaosRepository.saveAndFlush(chaos2);

        List<Chaos> chaoses = this.chaosRepository.findBySchedule(schedule1);
        assertThat(chaoses).containsExactly(chaos1);
    }

    @Test
    public void findByScheduleId() {
        Application application = new Application(UUID.randomUUID());
        this.applicationRepository.saveAndFlush(application);

        Schedule schedule1 = new Schedule("test-schedule-1", "test-name-1");
        this.scheduleRepository.saveAndFlush(schedule1);

        Chaos chaos1 = new Chaos(application, 0.1, schedule1);
        this.chaosRepository.saveAndFlush(chaos1);

        Schedule schedule2 = new Schedule("test-schedule-2", "test-name-2");
        this.scheduleRepository.saveAndFlush(schedule2);

        Chaos chaos2 = new Chaos(application, 0.1, schedule2);
        this.chaosRepository.saveAndFlush(chaos2);

        List<Chaos> chaoses = this.chaosRepository.findByScheduleId(schedule1.getId());
        assertThat(chaoses).containsExactly(chaos1);
    }

}
