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

package io.pivotal.strepsirrhini.chaosloris.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

/**
 * An application that is a candidate for chaos <p> <p/><b>This class is not threadsafe</b>
 */
@Entity
public class Application {

    @Column(nullable = false, unique = true)
    private UUID applicationId;

    @Column(nullable = false)
    @GeneratedValue
    @Id
    @JsonIgnore
    private Long id;

    /**
     * Create a new instance
     *
     * @param applicationId the Cloud Foundry application id
     */
    public Application(UUID applicationId) {
        this.applicationId = applicationId;
    }

    Application() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Application that = (Application) o;
        return Objects.equals(this.applicationId, that.applicationId) &&
            Objects.equals(this.id, that.id);
    }

    /**
     * Returns the application id
     *
     * @return the application id
     */
    public UUID getApplicationId() {
        return this.applicationId;
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

    @Override
    public int hashCode() {
        return Objects.hash(this.applicationId, this.id);
    }

    @Override
    public String toString() {
        return "Application{" +
            "applicationId=" + this.applicationId +
            ", id=" + this.id +
            '}';
    }

}
