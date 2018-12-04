/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.transport;

import org.openwms.common.location.LocationType;

import java.util.List;
import java.util.Optional;

/**
 * A TransportUnitTypeService offers methods to deal with {@link TransportUnitType}s.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
interface TransportUnitTypeService {

    /**
     * Find and return a {@link TransportUnitType} identified by its unique business key.
     *
     * @param type The type as String
     * @return The instance.
     */
    Optional<TransportUnitType> findByType(String type);

    /**
     * Returns a List of all {@link TransportUnitType}s.
     * 
     * @return A list of all {@link TransportUnitType}s
     */
    List<TransportUnitType> findAll();

    /**
     * Create a new {@link TransportUnitType}.
     * 
     * @param transportUnitType
     *            The type to be created
     * @return A new created {@link TransportUnitType} instance.
     */
    TransportUnitType create(TransportUnitType transportUnitType);

    /**
     * Delete already persisted {@link TransportUnitType} instances.
     * 
     * @param transportUnitTypes
     *            A collection of instances to be deleted.
     */
    void deleteType(TransportUnitType... transportUnitTypes);

    /**
     * Save an already existing instance of {@link TransportUnitType}.
     * 
     * @param transportUnitType
     *            The instance to be updated
     * @return The updated instance
     */
    TransportUnitType save(TransportUnitType transportUnitType);

    /**
     * Update the List of {@link org.openwms.common.domain.TypePlacingRule}s for the given {@link TransportUnitType} type.
     * 
     * @param type
     *            The {@link TransportUnitType} to update.
     * @param newAssigned
     *            A new List of {@link LocationType}s that are allowed for the {@link TransportUnitType}.
     * @param newNotAssigned
     *            A List of {@link LocationType}s. All {@link org.openwms.common.domain.TypePlacingRule}s will be removed which have one of
     *            this {@link LocationType}s and the requested {@link TransportUnitType} type.
     * @return The updated {@link TransportUnitType}.
     */
    TransportUnitType updateRules(String type, List<LocationType> newAssigned, List<LocationType> newNotAssigned);

    /**
     * Return a List of all {@link org.openwms.common.domain.Rule}s that belong to this {@link TransportUnitType} type.
     * 
     * @param transportUnitType
     *            The {@link TransportUnitType} to search for.
     * @return The requested List or <code>null</code> if no {@link Rule} was found.
     */
    List<Rule> loadRules(String transportUnitType);
}