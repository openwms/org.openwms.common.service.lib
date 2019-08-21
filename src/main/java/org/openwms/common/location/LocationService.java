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

import org.openwms.common.location.api.ErrorCodeVO;

import java.util.List;
import java.util.Optional;

/**
 * A LocationService offers useful methods according to the handling of {@link Location}s.
 *
 * @author Heiko Scherrer
 */
public interface LocationService {

    /**
     * Removes a list of {@link Message}s from a Location.
     *
     * @param pKey The persistent key identifier of the Location
     * @param messages The messages to be removed
     * @return The updated Location
     */
    Location removeMessages(String pKey, List<Message> messages);

    /**
     * Find and return a Location identified by the given {@code locationPK}.
     *
     * @param locationPK The business key of the Location to search for
     * @return The Location
     */
    Optional<Location> findByLocationId(LocationPK locationPK);

    /**
     * Find and return a Location identified by the given {@code locationPK}.
     *
     * @param locationPK The business key as String of the Location to search for
     * @return The Location
     */
    Optional<Location> findByLocationId(String locationPK);

    /**
     * Find and return all Locations that match the {@code locationPK} whereas the
     * attributes of the LocationPK may include SQL wildcard operators, like '%', '_'.
     *
     * @param locationPK The LocationPK
     * @return All Locations or an empty list, never {@literal null}
     */
    List<Location> findLocations(LocationPK locationPK);

    /**
     * Find and return a Location by the given {@code plcCode}.
     *
     * @param plcCode The PLC Code
     * @return The Location
     */
    Optional<Location> findByPlcCode(String plcCode);

    /**
     * Find and return all Locations that belong to a LocationGroup with the name {@code locationGroupName}.
     *
     * @param locationGroupName The name of the LocationGroup
     * @return All LocationGroups or an empty list, never {@literal null}
     */
    List<Location> findAllOf(List<String> locationGroupName);

    /**
     * Change the infeed and outfeed state of a {@link Location} in respect of the according {@code LocationGroup}.
     *
     * @param pKey The persistent key identifier of the Location to change
     * @param errorCode Contains the error bitmap to encode the state
     */
    void changeState(
            String pKey,
            ErrorCodeVO errorCode
    );
}
