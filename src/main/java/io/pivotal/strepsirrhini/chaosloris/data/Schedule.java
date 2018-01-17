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
import java.util.Objects;

/**
 * A schedule for driving chaos <p> <b>This class is not threadsafe</b>
 */
@Entity
public class Schedule {

    @Column(nullable = false)
    private String expression;

    @Column(nullable = false)
    @GeneratedValue
    @Id
    @JsonIgnore
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Create a new instance
     *
     * @param expression the cron expression of the schedule
     * @param name       the name of the schedule
     */
    public Schedule(String expression, String name) {
        this.name = name;
        this.expression = expression;
    }

    Schedule() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Schedule schedule = (Schedule) o;
        return Objects.equals(this.expression, schedule.expression) &&
            Objects.equals(this.id, schedule.id) &&
            Objects.equals(this.name, schedule.name);
    }

    /**
     * Returns the expression
     *
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }

    /**
     * Sets the expression
     *
     * @param expression the expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
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
     * Returns the name
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.expression, this.id, this.name);
    }

    @Override
    public String toString() {
        return "Schedule{" +
            "expression='" + this.expression + '\'' +
            ", id=" + this.id +
            ", name='" + this.name + '\'' +
            '}';
    }

}
