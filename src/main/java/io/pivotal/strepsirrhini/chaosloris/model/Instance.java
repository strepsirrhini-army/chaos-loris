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
import javax.persistence.MapKeyColumn;
import java.util.Map;
import java.util.UUID;

/**
 * An instance of the service
 */
@Entity
public final class Instance {

    @Id
    private volatile UUID id;

    private volatile UUID organizationId;

    @ElementCollection
    @CollectionTable(name = "instance_parameter", joinColumns = @JoinColumn(name = "instance"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private volatile Map<String, String> parameters;

    private volatile UUID planId;

    private volatile UUID serviceId;

    private volatile UUID spaceId;

    Instance() {
    }

    /**
     * Creates a new instance
     *
     * @param id             the id of the instance
     * @param organizationId the organization id of the instance
     * @param parameters     the parameters of the instance
     * @param planId         the plan id of the instance
     * @param serviceId      the service id of the instance
     * @param spaceId        the space id of the instance
     */
    public Instance(UUID id, UUID organizationId, Map<String, String> parameters, UUID planId, UUID serviceId,
                    UUID spaceId) {
        this.id = id;
        this.organizationId = organizationId;
        this.parameters = parameters;
        this.planId = planId;
        this.serviceId = serviceId;
        this.spaceId = spaceId;
    }

    /**
     * Returns the id of the instance
     *
     * @return the id of the instance
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Returns the organization id of the instance
     *
     * @return the organization id of the instance
     */
    public UUID getOrganizationId() {
        return this.organizationId;
    }

    /**
     * Returns the parameters of the instance
     *
     * @return the parameters of the instance
     */
    public Map<String, ?> getParameters() {
        return this.parameters;
    }

    /**
     * Returns the plan id of the instance
     *
     * @return the plan id of the instance
     */
    public UUID getPlanId() {
        return this.planId;
    }

    /**
     * Returns the service id of the instance
     *
     * @return the service id of the instance
     */
    public UUID getServiceId() {
        return this.serviceId;
    }

    /**
     * Returns the space id of the instance
     *
     * @return the space id of the instance
     */
    public UUID getSpaceId() {
        return this.spaceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instance instance = (Instance) o;

        if (!id.equals(instance.id)) return false;
        if (!organizationId.equals(instance.organizationId)) return false;
        if (!parameters.equals(instance.parameters)) return false;
        if (!planId.equals(instance.planId)) return false;
        if (!serviceId.equals(instance.serviceId)) return false;
        return spaceId.equals(instance.spaceId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + organizationId.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + planId.hashCode();
        result = 31 * result + serviceId.hashCode();
        result = 31 * result + spaceId.hashCode();
        return result;
    }
}
