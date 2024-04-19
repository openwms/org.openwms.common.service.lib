/*
 * Copyright 2005-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.location.impl.registration;

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A ReplicaRegistry.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_REPLICA_REGISTRY")
public class ReplicaRegistry extends ApplicationEntity implements Serializable {

    @Column(name = "C_APPLICATION_NAME", nullable = false)
    @NotBlank
    private String applicationName;

    @Column(name = "C_REQUEST_REMOVAL_ENDPOINT")
    private String requestRemovalEndpoint;

    @Column(name = "C_REMOVAL_ENDPOINT")
    private String removalEndpoint;

    @Column(name = "C_STATE", nullable = false)
    @NotBlank
    private String state;

    @Column(name = "C_REGISTERED_AT")
    private LocalDateTime registeredAt;

    @Column(name = "C_UNREGISTERED_AT")
    private LocalDateTime unRegisteredAt;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getRequestRemovalEndpoint() {
        return requestRemovalEndpoint;
    }

    public void setRequestRemovalEndpoint(String requestRemovalEndpoint) {
        this.requestRemovalEndpoint = requestRemovalEndpoint;
    }

    public String getRemovalEndpoint() {
        return removalEndpoint;
    }

    public void setRemovalEndpoint(String removalEndpoint) {
        this.removalEndpoint = removalEndpoint;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getUnRegisteredAt() {
        return unRegisteredAt;
    }

    public void setUnRegisteredAt(LocalDateTime unRegisteredAt) {
        this.unRegisteredAt = unRegisteredAt;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicaRegistry that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(applicationName, that.applicationName) && Objects.equals(requestRemovalEndpoint, that.requestRemovalEndpoint) && Objects.equals(removalEndpoint, that.removalEndpoint) && Objects.equals(state, that.state) && Objects.equals(registeredAt, that.registeredAt) && Objects.equals(unRegisteredAt, that.unRegisteredAt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), applicationName, requestRemovalEndpoint, removalEndpoint, state, registeredAt, unRegisteredAt);
    }
}
