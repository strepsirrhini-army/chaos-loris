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

package io.pivotal.strepsirrhini.chaosloris.data;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * An event signaling that a {@link Schedule} has been created
 */
public final class ScheduleCreatedEvent extends ApplicationEvent {

    private final Schedule schedule;

    /**
     * Create a new instance.
     *
     * @param source   the object on which the event initially occurred (never {@code null})
     * @param schedule the schedule undergoing a lifecycle event
     */
    public ScheduleCreatedEvent(Object source, Schedule schedule) {
        super(source);
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleCreatedEvent that = (ScheduleCreatedEvent) o;
        return Objects.equals(this.schedule, that.schedule) &&
            Objects.equals(getSource(), that.getSource());
    }

    /**
     * Returns the schedule
     *
     * @return the schedule
     */
    public Schedule getSchedule() {
        return this.schedule;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.schedule, getSource());
    }

    @Override
    public String toString() {
        return "ScheduleCreatedEvent{" +
            "schedule=" + this.schedule +
            ", source=" + getSource() +
            "} ";
    }

}
