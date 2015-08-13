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

package io.pivotal.strepsirrhini.chaosloris.model;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import java.util.Map;
import java.util.UUID;

/**
 * A binding to an instance of the service
 */
@Entity
public final class Binding {

    @Id
    private volatile UUID id;

    @ManyToOne
    private volatile Instance instance;

    private volatile UUID applicationId;

    @ElementCollection
    @CollectionTable(name = "binding_parameter", joinColumns = @JoinColumn(name = "binding_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private volatile Map<String, String> parameters;

    Binding() {
    }

    /**
     * Creates a new instance
     *
     * @param id            the id of the binding
     * @param instance      the instance the binding is for
     * @param applicationId the application id of the binding
     * @param parameters    the parameters of the binding
     */
    public Binding(UUID id, Instance instance, UUID applicationId, Map<String, String> parameters) {
        this.id = id;
        this.instance = instance;
        this.applicationId = applicationId;
        this.parameters = parameters;
    }

    /**
     * Returns the id of the binding
     *
     * @return the id of the binding
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Returns the instance the binding is for
     *
     * @return the instance the binding is for
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Returns the application id of the binding
     *
     * @return the application id of the binding
     */
    public UUID getApplicationId() {
        return this.applicationId;
    }

    /**
     * Returns the parameters of the binding
     *
     * @return the parameters of the binding
     */
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Binding binding = (Binding) o;

        if (!id.equals(binding.id)) return false;
        if (!instance.equals(binding.instance)) return false;
        if (!applicationId.equals(binding.applicationId)) return false;
        return parameters.equals(binding.parameters);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + instance.hashCode();
        result = 31 * result + applicationId.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

}
