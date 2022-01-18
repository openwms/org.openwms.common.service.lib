/*
 * Copyright 2005-2021 the original author or authors.
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATIONS;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION_TYPES;

/**
 * A LocationApi defines the public REST API to manage {@code Location}s. It is a Feign remote stub that can be used by client application.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "locationApi", decode404 = true)
public interface LocationApi {

    /**
     * Create a new {@code Location}.
     *
     * @param location The representation of the Location to create
     * @return The created instance
     */
    @PostMapping(API_LOCATIONS)
    LocationVO createLocation(@RequestBody LocationVO location);

    /**
     * Save a modified {@code Location}.
     *
     * @param location The representation of the modified Location to save
     * @return The updated and saved instance
     */
    @PutMapping(API_LOCATIONS)
    LocationVO updateLocation(@RequestBody LocationVO location);

    /**
     * Find and return a {@code Location}.
     *
     * @param pKey The persistent key
     * @return The instance
     * @throws org.ameba.exception.NotFoundException If no instance exists
     */
    @GetMapping(value = API_LOCATIONS + "/{pKey}")
    @Cacheable("locations")
    LocationVO findByPKey(@PathVariable("pKey") String pKey);

    /**
     * Find and return all {@code LocationTypes}.
     *
     * @return Never {@literal null}
     */
    @GetMapping(API_LOCATION_TYPES)
    @Cacheable("locations")
    List<LocationTypeVO> findAll();

    /**
     * Find and return a {@code Location} representation by the given {@code locationPK}.
     *
     * @param locationPK The business key of the Location
     * @return Never {@literal null}
     * @throws IllegalArgumentException in case the given locationPK is not valid
     */
    @GetMapping(value = API_LOCATIONS, params = {"locationPK"})
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
    @GetMapping(value = API_LOCATIONS, params = {"plcCode"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByPlcCode(
            @RequestParam("plcCode") String plcCode
    );

    /**
     * Find and return a {@code Location} representation by the given {@code erpCode}.
     *
     * @param erpCode The ERP code
     * @return Never {@literal null}
     */
    @GetMapping(value = API_LOCATIONS, params = {"erpCode"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByErpCode(
            @RequestParam("erpCode") String erpCode
    );

    /**
     * Find and return all {@code Location}s that belong to one or more {@code LocationGroup}s identified by their {@code locationGroupNames}.
     *
     * @param locationGroupNames A list of LocationGroup names
     * @return All Location instances or an empty list
     */
    @GetMapping(value = API_LOCATIONS, params = {"locationGroupNames"})
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
    @PatchMapping(value = API_LOCATION + "/{pKey}", params = "op=change-state")
    @CacheEvict(cacheNames = "locations", allEntries = true)
    void changeState(
            @PathVariable(name = "pKey") String pKey,
            @RequestParam(name = "op") String op,
            @RequestBody ErrorCodeVO errorCode
    );

    /**
     * Change the current {@code mode} a {@code Location}, identified by {@code erpCode}.
     *
     * @param erpCode The ERP code of the Location
     * @param type The type of lock to apply to the Location
     * @param mode The mode to apply to the Locations lock
     */
    @PostMapping(path = API_LOCATIONS , params = {"erpCode", "type!=PERMANENT_LOCK", "mode"})
    void changeState(
            @RequestParam("erpCode") String erpCode,
            @RequestParam("type") LockType type,
            @RequestParam("mode") LockMode mode,
            @RequestParam(value = "plcState", required = false) Integer plcState
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
    @GetMapping(value = API_LOCATIONS, params = {"area", "aisle", "x", "y", "z"})
    @Cacheable("locations")
    List<LocationVO> findLocations(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "aisle", required = false) String aisle,
            @RequestParam(value = "x", required = false) String x,
            @RequestParam(value = "y", required = false) String y,
            @RequestParam(value = "z", required = false) String z
    );
}
