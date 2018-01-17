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
 * An event signaling that a {@link Schedule} has been deleted
 */
public final class ScheduleDeletedEvent extends ApplicationEvent {

    private final Long id;

    /**
     * Create a new instance.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param id     the id of the deleted schedule
     */
    public ScheduleDeletedEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleDeletedEvent that = (ScheduleDeletedEvent) o;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(getSource(), that.getSource());
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, getSource());
    }

    @Override
    public String toString() {
        return "ScheduleDeletedEvent{" +
            "id=" + id +
            ", source=" + getSource() +
            '}';
    }

}
