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
package org.openwms.common.location;

import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.openwms.common.location.api.ValidationGroups;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * A LocationGroupService offers some useful methods regarding the general handling of {@link LocationGroup}s. <p> This interface is
 * declared generic typed that implementation classes can use any extension of {@link LocationGroup}s. </p>
 *
 * @author Heiko Scherrer
 */
public interface LocationGroupService {

    /**
     * Creates a new {@link LocationGroup} based on the provided {@link LocationGroupVO} representation.
     *
     * @param vo The {@link LocationGroupVO} containing the information for the new LocationGroup
     * @return The created LocationGroup
     */
    @NotNull LocationGroup create(@NotNull @Validated(ValidationGroups.Create.class) @Valid LocationGroupVO vo);

    /**
     * Change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param pKey The persisted key of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeGroupState(@NotBlank String pKey, @NotNull LocationGroupState stateIn, @NotNull LocationGroupState stateOut);

    /**
     * Change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param name The name of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     * @throws org.ameba.exception.NotFoundException In case it doesn't exist
     */
    void changeGroupStates(@NotBlank String name, Optional<LocationGroupState> stateIn, Optional<LocationGroupState> stateOut);

    /**
     * Change the operation mode of a {@link LocationGroup}.
     *
     * @param name The name of the LocationGroup to change
     * @param mode The new operation mode to set
     * @throws org.ameba.exception.NotFoundException In case it doesn't exist
     * @see org.openwms.common.location.api.LocationGroupMode for supported mode values
     */
    void changeOperationMode(@NotBlank String name, @NotBlank String mode);

    /**
     * Find and return a {@link LocationGroup} identified by its unique {@code name}.
     *
     * @param name The name to search for
     * @return The optional LocationGroup instance
     */
    Optional<LocationGroup> findByName(@NotBlank String name);

    /**
     * Find and return a {@link LocationGroup} identified by its unique {@code name}.
     *
     * @param name The name to search for
     * @return The LocationGroup, never {@literal null}
     * @throws org.ameba.exception.NotFoundException In case it doesn't exist
     */
    @NotNull LocationGroup findByNameOrThrow(@NotBlank String name);

    /**
     * Find and return all {@link LocationGroup}s.
     *
     * @return All existing instances or an empty List, never {@literal null}
     */
    @NotNull List<LocationGroup> findAll();

    /**
     * Find and return all {@link LocationGroup}s with the given {@code locationGroupNames}.
     *
     * @param locationGroupNames The names of the LocationGroups to search for
     * @return Always an list instance, never {@literal null}
     */
    @NotNull List<LocationGroup> findByNames(@NotEmpty List<String> locationGroupNames);

    /**
     * Updates a LocationGroup based on the provided persisted key and {@link LocationGroupVO} representation.
     *
     * @param pKey The persisted key of the LocationGroup to update (must not be blank)
     * @param locationGroupVO The updated information for the LocationGroup (must not be null)
     * @return The updated LocationGroup
     */
    @NotNull LocationGroup update(@NotBlank String pKey, @NotNull LocationGroupVO locationGroupVO);
}