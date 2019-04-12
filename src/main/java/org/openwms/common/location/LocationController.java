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

import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationVO;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!INMEM")
@RestController
public class LocationController {

    private final LocationService locationService;
    private final ErrorCodeTransformers.LocationStateIn stateIn;
    private final ErrorCodeTransformers.LocationStateOut stateOut;
    private final BeanMapper mapper;

    LocationController(LocationService locationService, ErrorCodeTransformers.LocationStateIn stateIn, ErrorCodeTransformers.LocationStateOut stateOut, BeanMapper mapper) {
        this.locationService = locationService;
        this.stateIn = stateIn;
        this.stateOut = stateOut;
        this.mapper = mapper;
    }

    @GetMapping(value = "/v1/locations", params = {"locationPK"})
    public Optional<LocationVO> findLocationByCoordinate(@RequestParam("locationPK") String locationPK) {
        if (!LocationPK.isValid(locationPK)) {
            throw new NotFoundException(format("Invalid location [%s]", locationPK));
        }
        Location location = locationService.findByLocationId(LocationPK.fromString(locationPK)).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", locationPK)));
        return Optional.ofNullable(mapper.map(location, LocationVO.class));
    }

    @GetMapping(value = "/v1/locations", params = {"plcCode"})
    public Optional<LocationVO> findLocationByPlcCode(@RequestParam("plcCode") String plcCode) {
        Location location = locationService.findByPlcCode(plcCode).orElseThrow(() -> new NotFoundException(format("No Location with PLC Code [%s] found", plcCode)));
        return Optional.ofNullable(mapper.map(location, LocationVO.class));
    }

    @GetMapping(value = "/v1/locations", params = {"locationGroupNames"})
    public List<LocationVO> findLocationsForLocationGroups(@RequestParam("locationGroupNames") List<String> locationGroupNames) {
        List<Location> locations = locationService.findAllOf(locationGroupNames);
        return mapper.map(locations, LocationVO.class);
    }

    @PatchMapping(value = "/v1/locations/{pKey}")
    public void updateState(@PathVariable(name = "pKey") String pKey, @RequestBody ErrorCodeVO errorCode) {
        locationService.changeState(pKey, stateIn, stateOut, errorCode);
    }

    @GetMapping(value = "/v1/locations", params = {"area", "aisle", "x", "y", "z"})
    public List<LocationVO> findLocations(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "aisle", required = false) String aisle,
            @RequestParam(value = "x", required = false) String x,
            @RequestParam(value = "y", required = false) String y,
            @RequestParam(value = "z", required = false) String z
    ) {
        LocationPK pk = LocationPK.newBuilder()
                .area(area == null ? "%" : area)
                .aisle(aisle == null ? "%" : aisle)
                .x(x == null ? "%" : x)
                .y(y == null ? "%" : y)
                .z(z == null ? "%" : z)
                .build();
        List<Location> locations = locationService.findLocations(pk);
        return mapper.map(locations, LocationVO.class);
    }

}
