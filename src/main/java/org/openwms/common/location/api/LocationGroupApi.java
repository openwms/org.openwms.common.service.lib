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
 * A LocationGroupApi.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "locationGroupApi", decode404 = true)
public interface LocationGroupApi {

    /** API version. */
    String API_VERSION = "v1";
    /** API root to hit a LocationGroup. */
    String API_LOCATION_GROUP = "/" + API_VERSION + "/location-group";
    /** API root to hit LocationGroups (plural). */
    String API_LOCATION_GROUPS = "/" + API_VERSION + "/location-groups";

    /**
     * Find a {@code LocationGroup} with the given {@code name}.
     *
     * @param name The name of the LocationGroup
     * @return The instance or may result in a 404-Not Found
     */
    @GetMapping(value = API_LOCATION_GROUPS, params = {"name"})
    @Cacheable("locationGroups")
    Optional<LocationGroupVO> findByName(
            @RequestParam("name") String name
    );

    /**
     * Find all {@code LocationGroup}s with the given {@code name}s.
     *
     * @param names Names of all LocationGroups
     * @return A list of instances or an empty list but never {@literal null}
     */
    @GetMapping(value = API_LOCATION_GROUPS, params = {"names"})
    @Cacheable("locationGroups")
    List<LocationGroupVO> findByNames(
            @RequestParam("names") List<String> names
    );

    /**
     * Find and return all existing {@code LocationGroup}s.
     *
     * @return Never {@literal null}
     */
    @GetMapping(API_LOCATION_GROUPS)
    List<LocationGroupVO> findAll();

    /**
     * Change the state of an existing {@code LocationGroup}.
     *
     * @param name The name of the LocationGroup
     * @param errorCode The decoded state
     */
    @PatchMapping(value = API_LOCATION_GROUPS, params = {"name", "op=change-state"})
    void changeGroupState(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "op") String op,
            @RequestBody ErrorCodeVO errorCode
    );

    /**
     * Change the state of an existing {@code LocationGroup}.
     *
     * @param pKey The persistent key of the LocationGroup
     * @param stateIn The inbound state to set
     * @param stateOut The outbound state to set
     */
    @PatchMapping(value = API_LOCATION_GROUP + "/{pKey}", params = "op=change-state")
    void changeGroupState(
            @PathVariable("pKey") String pKey,
            @RequestParam(name = "op") String op,
            @RequestParam(name = "statein", required = false) LocationGroupState stateIn,
            @RequestParam(name = "stateout", required = false) LocationGroupState stateOut
    );
}
