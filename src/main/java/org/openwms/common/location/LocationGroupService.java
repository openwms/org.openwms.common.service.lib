/*
 * Copyright 2005-2019 the original author or authors.
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
     * Change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param pKey The persisted key of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeGroupState(String pKey, LocationGroupState stateIn, LocationGroupState stateOut);

    /**
     * Change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param name The name of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeGroupStates(String name, Optional<LocationGroupState> stateIn, Optional<LocationGroupState> stateOut);

    /**
     * Change the operation mode of a {@link LocationGroup}.
     *
     * @param name The name of the LocationGroup to change
     * @param mode The new operation mode to set
     * @see org.openwms.common.location.api.LocationGroupMode for supported mode values
     */
    void changeOperationMode(String name, String mode);

    /**
     * Find and return a {@link LocationGroup} by its unique {@code name}.
     *
     * @param name The name to search for
     * @return The optional LocationGroup
     */
    Optional<LocationGroup> findByName(String name);

    /**
     * Find and return all {@link LocationGroup}s.
     *
     * @return All existing instances
     */
    List<LocationGroup> findAll();

    /**
     * Find and return all {@link LocationGroup}s with the given {@code locationGroupNames}.
     *
     * @param locationGroupNames The names of the LocationGroups to search for
     * @return Always an list instance, never {@literal null}
     */
    List<LocationGroup> findByNames(List<String> locationGroupNames);
}