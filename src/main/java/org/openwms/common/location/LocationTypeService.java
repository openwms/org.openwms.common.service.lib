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
package org.openwms.common.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A LocationTypeService offers useful methods according to the handling of {@link LocationType}s.
 *
 * @author Heiko Scherrer
 */
public interface LocationTypeService {

    /**
     * Find and return a {@link LocationType}.
     *
     * @param pKey The persistent key
     * @return The instance
     * @throws org.ameba.exception.NotFoundException If the LocationType does not exist
     */
    @NotNull LocationType findByPKey(@NotBlank String pKey);

    /**
     * Find a {@code LocationType} by it's name.
     *
     * @param typeName The name of the LocationType
     * @return The instance
     */
    Optional<LocationType> findByTypeName(@NotBlank String typeName);

    /**
     * Return a list of all {@code LocationType}s in natural order.
     *
     * @return All LocationTypes as a list
     */
    @NotNull List<LocationType> findAll();

    /**
     * Delete already persisted {@code LocationType}s.
     *
     * @param locationTypes A list of all instances to be deleted.
     */
    void delete(@NotNull List<LocationType> locationTypes);

    /**
     * Save a {@code LocationType}.
     *
     * @param locationType The type to save
     * @return The saved type
     */
    @NotNull LocationType save(@NotNull LocationType locationType);
}
