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
package org.openwms.common.location.api;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * A LocationApi deals with {@code Location}s.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "locationApi", decode404 = true)
public interface LocationApi {

    /**
     * Find and return a {@code Location} representation by the given {@code locationPK}.
     *
     * @param locationPK The business key of the Location
     * @return Never {@literal null}
     * @throws IllegalArgumentException in case the given locationPK is not valid
     */
    @GetMapping(value = LocationApiConstants.API_LOCATIONS, params = {"locationPK"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByCoordinate(
            @RequestParam("locationPK") String locationPK
    );

    /**
     * Find and return a {@code Location} representation by the given {@code plcCode}.
     *
     * @param plcCode The PLC code
     * @return Never {@literal null}
     */
    @GetMapping(value = LocationApiConstants.API_LOCATIONS, params = {"plcCode"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByPlcCode(
            @RequestParam("plcCode") String plcCode
    );

    /**
     * Find and return all {@code Location}s that belong to one or more {@code LocationGroup}s identified by their {@code locationGroupNames}.
     *
     * @param locationGroupNames A list of LocationGroup names
     * @return All Location instances or an empty list
     */
    @GetMapping(value = LocationApiConstants.API_LOCATIONS, params = {"locationGroupNames"})
    @Cacheable("locations")
    List<LocationVO> findLocationsForLocationGroups(
            @RequestParam("locationGroupNames") List<String> locationGroupNames
    );

    /**
     * Change the state of a a {@code Location}.
     *
     * @param pKey The persistent key of the Location
     * @param errorCode The decoded state
     */
    @PatchMapping(value = LocationApiConstants.API_LOCATION + "/{pKey}", params = "op=change-state")
    @CacheEvict(cacheNames = "locations", allEntries = true)
    void changeState(
            @PathVariable(name = "pKey") String pKey,
            @RequestParam(name = "op") String op,
            @RequestBody ErrorCodeVO errorCode
    );

    /**
     * Find and return all {@code Location}s that match the given criteria expressed by area/aisle/x/y/z. Supported wildcards in coordinates: {@code %,_}.
     *
     * @param area The Area to search for
     * @param aisle The Aisle to search for
     * @param x The X to search for or
     * @param y The Y to search for or
     * @param z The Z to search for or
     * @return All Location instances or an empty list
     */
    @GetMapping(value = LocationApiConstants.API_LOCATIONS, params = {"area", "aisle", "x", "y", "z"})
    @Cacheable("locations")
    List<LocationVO> findLocations(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "aisle", required = false) String aisle,
            @RequestParam(value = "x", required = false) String x,
            @RequestParam(value = "y", required = false) String y,
            @RequestParam(value = "z", required = false) String z
    );
}
