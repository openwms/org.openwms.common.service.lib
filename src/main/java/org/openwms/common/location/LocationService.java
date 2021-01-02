/*
 * Copyright 2005-2020 the original author or authors.
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
 * A LocationService offers useful methods according to the handling of {@code Location}s.
 *
 * @author Heiko Scherrer
 */
public interface LocationService {

    /**
     * Find and return a {@code Location}.
     *
     * @param locationId The business key of the Location to search for
     * @return The Location instance
     */
    Optional<Location> findByLocationId(LocationPK locationId);

    /**
     * Find and return a {@code Location}.
     *
     * @param locationId The business key as String of the Location to search for
     * @return The Location instance
     */
    Optional<Location> findByLocationId(String locationId);

    /**
     * Find and return all {@code Location}s that match the {@code locationIds} whereas the attributes of the {@code LocationPK} may include
     * SQL wildcard operators, like '%', '_'.
     *
     * @param locationIds The business keys of the Locations to search for
     * @return The Location instances or an empty list, never {@literal null}
     */
    List<Location> findLocations(LocationPK locationIds);

    /**
     * Find and return a {@code Location}.
     *
     * @param plcCode The PLC Code to search for the Location
     * @return The Location instance
     */
    Optional<Location> findByPlcCode(String plcCode);

    /**
     * Find and return all Locations that belong to a {@code LocationGroup}.
     *
     * @param locationGroupNames One ore names identifying the LocationGroups to search Locations for
     * @return The LocationGroup instances or an empty list, never {@literal null}
     */
    List<Location> findAllOf(List<String> locationGroupNames);

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

    /**
     * Persist a new entity or merge if just exists.
     * @param location new or updated location.
     * @return location entity.
     */
    Location save(Location location);
}
