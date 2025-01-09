/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.transport.impl;

import org.openwms.common.transport.TransportUnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A TransportUnitTypeRepository adds particular functionality regarding {@link TransportUnitType} entity classes.
 *
 * @author Heiko Scherrer
 */
@Repository
interface TransportUnitTypeRepository extends JpaRepository<TransportUnitType, Long> {

    Optional<TransportUnitType> findBypKey(String persistentKey);

    /**
     * Find and return a TransportUnitType by the given {@literal type}.
     *
     * @param type The type to identify the TransportUnitType
     * @return The TransportUnitType instance
     */
    Optional<TransportUnitType> findByType(String type);
}