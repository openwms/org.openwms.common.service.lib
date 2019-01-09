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
package org.openwms.common.location.api;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * A LocationApi deals with {@code Location}s.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
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
    @GetMapping(value = "/v1/locations", params = {"locationPK"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByCoordinate(@RequestParam("locationPK") String locationPK);

    /**
     * Find and return a {@code Location} representation by the given {@code plcCode}.
     *
     * @param plcCode The PLC code
     * @return Never {@literal null}
     */
    @GetMapping(value = "/v1/locations", params = {"plcCode"})
    @Cacheable("locations")
    Optional<LocationVO> findLocationByPlcCode(@RequestParam("plcCode") String plcCode);

    /**
     * Find and return all {@link LocationVO}s that belong to one or more
     * {@code LocationGroupVO}s identified by their {@code locationGroupNames}.
     *
     * @param locationGroupNames A list of LocationGroup names.
     * @return All Location instances or an empty list
     */
    @GetMapping(value = "/v1/locations", params = {"locationGroupNames"})
    @Cacheable("locations")
    List<LocationVO> findLocationsForLocationGroups(@RequestParam("locationGroupNames") List<String> locationGroupNames);
}
