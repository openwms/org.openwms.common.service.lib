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

import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;

import java.util.List;
import java.util.Optional;

/**
 * A LocationService offers some useful methods regarding the general handling of {@link Location}s. <p> This interface is declared generic
 * typed that implementation classes can use any extension of {@link Location}s. </p>
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface LocationService {

    /**
     * Return a list of all {@link Location}s not sorted and not filtered in natural order.
     *
     * @return All {@link Location}s as a list
     */
    List<Location> getAllLocations();

    /**
     * Removes a list of {@link Message}s from a Location.
     *
     * @param id The technical key of the Location
     * @param messages The messages to be removed
     * @return The updated Location
     */
    Location removeMessages(Long id, List<Message> messages);

    /**
     * Return a list of all {@link LocationType}s not sorted and not filtered in natural order.
     *
     * @return All {@link LocationType}s as a list
     */
    List<LocationType> getAllLocationTypes();

    /**
     * Delete already persisted {@link LocationType} instances.
     *
     * @param locationTypes A list of all instances to be deleted.
     */
    void deleteLocationTypes(List<LocationType> locationTypes);

    /**
     * Saves a {@link LocationType}.
     *
     * @param locationType The type to save
     * @return The saved type
     */
    LocationType saveLocationType(LocationType locationType);

    /**
     * Find and return a Location identified by the given {@code locationPK}.
     *
     * @param locationPK The business key of the Location to search for
     * @return The Location
     * @throws org.ameba.exception.NotFoundException if entity not found
     */
    Optional<Location> findByLocationId(LocationPK locationPK);

    /**
     * Find and return all Locations that match the {@code locationPK} whereas the
     * attributes of the LocationPK may include SQL wildcard operators, like '%', '_'.
     *
     * @param locationPK The LocationPK
     * @return All Locations or an empty list, never {@literal null}
     */
    List<Location> findLocations(LocationPK locationPK);

    /**
     * Find and return a {@link Location} by the given {@code plcCode}.
     *
     * @param plcCode The PLC Code
     * @return The Location
     */
    Optional<Location> findByPlcCode(String plcCode);

    /**
     * Find and return a Location identified by the given {@code locationPK}.
     *
     * @param locationPK The business key of the Location to search for
     * @return The Location
     * @throws org.ameba.exception.NotFoundException if entity not found
     */
    Location findByLocationId(String locationPK);

    /**
     * Find and return a Location {@link Location} by the given {@code location}.
     *
     * @param location The LocationPK or the PLC Code
     * @return The Location
     */
    Optional<Location> findByLocationIdOrPlcCode(String location);

    /**
     * Find and return all Locations that belong to a LocationGroup with the name {@code locationGroupName}.
     *
     * @param locationGroupName The name of the LocationGroup
     * @return All LocationGroups or an empty list, never {@literal null}
     */
    List<Location> findAllOf(List<String> locationGroupName);

    /**
     * Change the infeed and outfeed state of a {@link Location} in respect of the
     * according {@code LocationGroup}.
     *
     * @param pKey     The persistent key identifier of the Location to change
     * @param stateIn  The new infeed state
     * @param stateOut The new outfeed state
     */
    void changeState(String pKey, ErrorCodeTransformers.LocationStateIn stateIn, ErrorCodeTransformers.LocationStateOut stateOut, ErrorCodeVO errorCode);
}
