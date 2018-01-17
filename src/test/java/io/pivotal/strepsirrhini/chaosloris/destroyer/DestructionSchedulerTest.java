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

import io.pivotal.strepsirrhini.chaosloris.data.Schedule;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleCreatedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleDeletedEvent;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleRepository;
import io.pivotal.strepsirrhini.chaosloris.data.ScheduleUpdatedEvent;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Collections;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

public final class DestructionSchedulerTest {

    private final DestroyerFactory destroyerFactory = mock(DestroyerFactory.class, RETURNS_SMART_NULLS);

    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class, RETURNS_SMART_NULLS);

    private final TaskScheduler taskScheduler = mock(TaskScheduler.class, RETURNS_SMART_NULLS);

    private final DestructionScheduler destructionScheduler = new DestructionScheduler(this.destroyerFactory, this.scheduleRepository, this.taskScheduler);

    @Test
    public void healthDown() {
        this.destructionScheduler.start();
        this.destructionScheduler.stop();

        assertThat(this.destructionScheduler.health().getStatus()).isEqualTo(DOWN);
    }

    @Test
    public void healthUp() {
        this.destructionScheduler.start();

        assertThat(this.destructionScheduler.health().getStatus()).isEqualTo(UP);
    }

    @Test
    public void isRunningFalse() {
        this.destructionScheduler.start();
        this.destructionScheduler.stop();

        assertThat(this.destructionScheduler.isRunning()).isFalse();
    }

    @Test
    public void isRunningTrue() {
        this.destructionScheduler.start();

        assertThat(this.destructionScheduler.isRunning()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleCreated() {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-1L);

        Destroyer destroyer = mock(Destroyer.class, RETURNS_SMART_NULLS);
        ScheduledFuture future = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        when(this.destroyerFactory.create(schedule.getId())).thenReturn(destroyer);
        when(this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()))).thenReturn(future);

        this.destructionScheduler.scheduleCreated(new ScheduleCreatedEvent(this, schedule));
        verifyZeroInteractions(future);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleDeleted() {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-1L);

        Destroyer destroyer = mock(Destroyer.class, RETURNS_SMART_NULLS);
        ScheduledFuture future = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        when(this.destroyerFactory.create(schedule.getId())).thenReturn(destroyer);
        when(this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()))).thenReturn(future);

        this.destructionScheduler.scheduleCreated(new ScheduleCreatedEvent(this, schedule));
        this.destructionScheduler.scheduleDeleted(new ScheduleDeletedEvent(this, schedule.getId()));

        verify(future).cancel(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleUpdated() {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-1L);

        Destroyer destroyer = mock(Destroyer.class, RETURNS_SMART_NULLS);
        ScheduledFuture future1 = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        ScheduledFuture future2 = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        when(this.destroyerFactory.create(schedule.getId())).thenReturn(destroyer);
        when(this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()))).thenReturn(future1, future2);

        this.destructionScheduler.scheduleCreated(new ScheduleCreatedEvent(this, schedule));
        this.destructionScheduler.scheduleUpdated(new ScheduleUpdatedEvent(this, schedule));

        verify(future1).cancel(false);
        verifyZeroInteractions(future2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void start() {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-1L);

        Destroyer destroyer = mock(Destroyer.class, RETURNS_SMART_NULLS);
        ScheduledFuture future = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        when(this.destroyerFactory.create(schedule.getId())).thenReturn(destroyer);
        when(this.scheduleRepository.findAll()).thenReturn(Collections.singletonList(schedule));
        when(this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()))).thenReturn(future);

        this.destructionScheduler.start();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stop() {
        Schedule schedule = new Schedule("0 0 * * * *", "hourly");
        schedule.setId(-1L);

        Destroyer destroyer = mock(Destroyer.class, RETURNS_SMART_NULLS);
        ScheduledFuture future = mock(ScheduledFuture.class, RETURNS_SMART_NULLS);
        when(this.destroyerFactory.create(schedule.getId())).thenReturn(destroyer);
        when(this.scheduleRepository.findAll()).thenReturn(Collections.singletonList(schedule));
        when(this.taskScheduler.schedule(destroyer, new CronTrigger(schedule.getExpression()))).thenReturn(future);

        this.destructionScheduler.start();
        this.destructionScheduler.stop();

        verify(future).cancel(false);
    }

}
