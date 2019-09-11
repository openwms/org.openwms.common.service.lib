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

import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.CommonConstants;
import org.openwms.common.Index;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationVO;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.openwms.common.CommonConstants.API_LOCATION;
import static org.openwms.common.CommonConstants.API_LOCATIONS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A LocationController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@RestController
public class LocationController extends AbstractWebController {

    private final LocationService locationService;
    private final BeanMapper mapper;

    LocationController(LocationService locationService, BeanMapper mapper) {
        this.locationService = locationService;
        this.mapper = mapper;
    }

    @GetMapping(value = API_LOCATIONS, params = {"locationPK"})
    public ResponseEntity<Optional<LocationVO>> findLocationByCoordinate(@RequestParam("locationPK") String locationPK) {
        if (!LocationPK.isValid(locationPK)) {
            // here we need to throw an NFE because Feign needs to cast it into an Optional. IAE won't work!
            throw new NotFoundException(format("Invalid location [%s]", locationPK));
        }
        Location location = locationService.findByLocationId(LocationPK.fromString(locationPK)).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", locationPK)));
        return ResponseEntity.ok(Optional.ofNullable(mapper.map(location, LocationVO.class)));
    }

    @GetMapping(value = API_LOCATIONS, params = {"plcCode"})
    public ResponseEntity<Optional<LocationVO>> findLocationByPlcCode(@RequestParam("plcCode") String plcCode) {
        Location location = locationService.findByPlcCode(plcCode).orElseThrow(() -> new NotFoundException(format("No Location with PLC Code [%s] found", plcCode)));
        return ResponseEntity.ok(Optional.ofNullable(mapper.map(location, LocationVO.class)));
    }

    @GetMapping(value = API_LOCATIONS, params = {"locationGroupNames"})
    public ResponseEntity<List<LocationVO>> findLocationsForLocationGroups(@RequestParam("locationGroupNames") List<String> locationGroupNames) {
        List<Location> locations = locationService.findAllOf(locationGroupNames);
        return ResponseEntity.ok(mapper.map(locations, LocationVO.class));
    }

    @PatchMapping(value = API_LOCATION + "/{pKey}", params = "op=change-state")
    public ResponseEntity<Void> changeState(
            @PathVariable(name = "pKey") String pKey,
            @RequestBody ErrorCodeVO errorCode
    ) {
        locationService.changeState(pKey, errorCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = API_LOCATIONS, params = {"area", "aisle", "x", "y", "z"})
    public ResponseEntity<List<LocationVO>> findLocations(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "aisle", required = false) String aisle,
            @RequestParam(value = "x", required = false) String x,
            @RequestParam(value = "y", required = false) String y,
            @RequestParam(value = "z", required = false) String z
    ) {
        LocationPK pk = LocationPK.newBuilder()
                .area(area == null || area.equals("") ? "%" : area)
                .aisle(aisle == null || aisle.equals("") ? "%" : aisle)
                .x(x == null || x.equals("") ? "%" : x)
                .y(y == null || y.equals("") ? "%" : y)
                .z(z == null || z.equals("") ? "%" : z)
                .build();
        List<Location> locations = locationService.findLocations(pk);
        return locations.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(mapper.map(locations, LocationVO.class));
    }

    @GetMapping(CommonConstants.API_LOCATIONS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(LocationController.class).findLocationByCoordinate("AREA/AISLE/X/Y/Z")).withRel("location-findbycoordinate"),
                        linkTo(methodOn(LocationController.class).findLocationByPlcCode("PLC_CODE")).withRel("location-findbyplccode"),
                        linkTo(methodOn(LocationController.class).findLocationsForLocationGroups(asList("LG1", "LG2"))).withRel("location-forlocationgroup"),
                        linkTo(methodOn(LocationController.class).changeState("pKey", ErrorCodeVO.LOCK_STATE_IN_AND_OUT)).withRel("location-changestate")
                )
        );
    }
}
