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
package org.openwms.common.location;

import java.util.List;
import java.util.Optional;

/**
 * A LocationGroupService offers some useful methods regarding the general handling of {@link LocationGroup}s. <p> This interface is
 * declared generic typed that implementation classes can use any extension of {@link LocationGroup}s. </p>
 *
 * @param <T> Any kind of {@link LocationGroup}
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface LocationGroupService<T extends LocationGroup> {

    /**
     * Tries to change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param id The persisted key of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeGroupState(String id, LocationGroupState stateIn, LocationGroupState stateOut);

    /**
     * Tries to change the infeed and outfeed state of a {@link LocationGroup}.
     *
     * @param locationGroupName The name of the LocationGroup to change
     * @param stateIn The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeGroupStates(String locationGroupName, LocationGroupState stateIn, LocationGroupState stateOut);

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
     * Returns a hierarchical Tree of all {@link LocationGroup}s. Used by the user interface to show all {@link LocationGroup}s in a tree
     * form.
     *
     * @return All {@link LocationGroup}s as hierarchical tree
     */
    //TreeNode<T> getLocationGroupsAsTree();

    /**
     * Returns a List of all {@link LocationGroup}s.
     *
     * @return All {@link LocationGroup}s as a list
     */
    //List<T> getLocationGroupsAsList();

    /**
     * Save an already persisted {@link LocationGroup} and return the saved instance.
     *
     * @param locationGroup The {@link LocationGroup} to save
     * @return The saved {@link LocationGroup}
     */
    //T save(T locationGroup);
}