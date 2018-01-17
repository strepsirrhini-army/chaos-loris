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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * A definition of chaos to be performed <p> <b>This class is not threadsafe</b>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "schedule_id"}))
public class Chaos {

    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private Application application;

    @Column(nullable = false)
    @GeneratedValue
    @Id
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    private Double probability;

    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private Schedule schedule;

    /**
     * Create a new instance
     *
     * @param application the application to apply chaos to
     * @param probability the probability of an instance being destroyed
     * @param schedule    the schedule to apply chaos on
     */
    public Chaos(Application application, Double probability, Schedule schedule) {
        this.application = application;
        this.probability = probability;
        this.schedule = schedule;
    }

    Chaos() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chaos chaos = (Chaos) o;
        return Objects.equals(this.application, chaos.application) &&
            Objects.equals(this.id, chaos.id) &&
            Objects.equals(this.probability, chaos.probability) &&
            Objects.equals(this.schedule, chaos.schedule);
    }

    /**
     * Returns the application
     *
     * @return the application
     */
    public Application getApplication() {
        return this.application;
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
     * Returns the probability
     *
     * @return the probability
     */
    public Double getProbability() {
        return this.probability;
    }

    /**
     * Sets the probability
     *
     * @param probability the probability
     */
    public void setProbability(Double probability) {
        this.probability = probability;
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
        return Objects.hash(this.application, this.id, this.probability, this.schedule);
    }

    @Override
    public String toString() {
        return "Chaos{" +
            "application=" + this.application +
            ", id=" + this.id +
            ", probability=" + this.probability +
            ", schedule=" + this.schedule +
            '}';
    }

}
