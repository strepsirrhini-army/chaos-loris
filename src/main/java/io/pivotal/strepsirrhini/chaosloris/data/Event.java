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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * An event representing the execution of chaos <p> <b>This class is not threadsafe</b>
 */
@Entity
public class Event {

    @JoinColumn(nullable = false)
    @ManyToOne
    @JsonIgnore
    private Chaos chaos;

    @Column(nullable = false)
    private Instant executedAt;

    @Column(nullable = false)
    @GeneratedValue
    @Id
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    @ElementCollection
    @OrderBy
    private List<Integer> terminatedInstances;

    @Column(nullable = false)
    private Integer totalInstanceCount;

    public Event(Chaos chaos, Instant executedAt, List<Integer> terminatedInstances, Integer totalInstanceCount) {
        this.chaos = chaos;
        this.executedAt = executedAt;
        this.terminatedInstances = terminatedInstances;
        this.totalInstanceCount = totalInstanceCount;
    }

    Event() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(this.chaos, event.chaos) &&
            Objects.equals(this.executedAt, event.executedAt) &&
            Objects.equals(this.id, event.id) &&
            Objects.equals(this.terminatedInstances, event.terminatedInstances) &&
            Objects.equals(this.totalInstanceCount, event.totalInstanceCount);
    }

    /**
     * Returns the chaos
     *
     * @return the chaos
     */
    public Chaos getChaos() {
        return this.chaos;
    }

    /**
     * Returns when the event was executed
     *
     * @return when the event was executed
     */
    public Instant getExecutedAt() {
        return this.executedAt;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the number of instances terminated
     *
     * @return the number of instances terminated
     */
    public int getTerminatedInstanceCount() {
        return this.getTerminatedInstances().size();
    }

    /**
     * Returns the terminated instances
     *
     * @return the terminated instances
     */
    public List<Integer> getTerminatedInstances() {
        return this.terminatedInstances;
    }

    /**
     * Returns the total instance count
     *
     * @return the total instance count
     */
    public Integer getTotalInstanceCount() {
        return this.totalInstanceCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.chaos, this.executedAt, this.id, this.terminatedInstances, this.totalInstanceCount);
    }

    @Override
    public String toString() {
        return "Event{" +
            "chaos=" + this.chaos +
            ", executedAt=" + this.executedAt +
            ", id=" + this.id +
            ", terminatedInstances=" + this.terminatedInstances +
            ", totalInstanceCount=" + this.totalInstanceCount +
            '}';
    }

}
