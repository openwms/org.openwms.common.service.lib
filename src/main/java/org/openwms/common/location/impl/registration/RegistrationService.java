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

import jakarta.validation.constraints.NotNull;
import org.openwms.common.location.api.commands.LocationReplicaRegistration;

import java.util.List;

/**
 * A RegistrationService is responsible to manage registrations of foreign services that participate in the lifecycle of Locations and
 * LocationGroups.
 *
 * @author Heiko Scherrer
 */
public interface RegistrationService {

    /**
     * Registers a new service instance with their particular lifecycle endpoints.
     *
     * @param registration Contains all required registration information
     */
    void register(@NotNull LocationReplicaRegistration registration);

    /**
     * Unregisters a service instance.
     *
     * @param registration Contains all required information to unregister
     */
    void unregister(@NotNull LocationReplicaRegistration registration);

    /**
     * Get all currently registered service instances.
     *
     * @return A list of registrations
     */
    @NotNull List<ReplicaRegistry> getAllRegistered();
}
