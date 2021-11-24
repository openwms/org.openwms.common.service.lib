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
package org.openwms.common.location;

import org.ameba.exception.BusinessRuntimeException;
import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.location.api.LockMode;
import org.openwms.common.location.api.LockType;
import org.openwms.common.location.api.ValidationGroups;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;
import static org.openwms.common.CommonMessageCodes.LOCATION_ID_INVALID;
import static org.openwms.common.CommonMessageCodes.LOCATION_NOT_FOUND;
import static org.openwms.common.CommonMessageCodes.LOCATION_NOT_FOUND_BY_ERP_CODE;
import static org.openwms.common.CommonMessageCodes.LOCATION_NOT_FOUND_BY_PLC_CODE;
import static org.openwms.common.CommonMessageCodes.LOCK_MODE_UNSUPPORTED;
import static org.openwms.common.CommonMessageCodes.LOCK_TYPE_UNSUPPORTED;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATIONS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A LocationController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@Validated
@MeasuredRestController
public class LocationController extends AbstractWebController {

    private final BeanMapper mapper;
    private final Translator translator;
    private final LocationService locationService;

    LocationController(LocationService locationService, BeanMapper mapper, Translator translator) {
        this.locationService = locationService;
        this.mapper = mapper;
        this.translator = translator;
    }

    @PostMapping(API_LOCATIONS)
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<LocationVO> createLocation(@Valid @RequestBody LocationVO location, HttpServletRequest req) {
        var created = locationService.create(mapper.map(location, Location.class));
        return ResponseEntity
                .created(super.getLocationURIForCreatedResource(req, created.getPersistentKey()))
                .body(mapper.map(created, LocationVO.class));
    }

    @GetMapping(value = API_LOCATIONS, params = {"locationPK"})
    public ResponseEntity<Optional<LocationVO>> findLocationByCoordinate(@RequestParam("locationPK") String locationPK) {
        if (!LocationPK.isValid(locationPK)) {
            // here we need to throw an NFE because Feign needs to cast it into an Optional. IAE won't work!
            throw new NotFoundException(translator, LOCATION_ID_INVALID, new String[]{locationPK}, locationPK);
        }
        Location location = locationService.findByLocationPk(LocationPK.fromString(locationPK))
                .orElseThrow(() -> new NotFoundException(
                        translator,
                        LOCATION_NOT_FOUND,
                        new String[]{locationPK},
                        locationPK
                ));
        return ResponseEntity.ok(Optional.ofNullable(mapper.map(location, LocationVO.class)));
    }

    @GetMapping(value = API_LOCATIONS, params = {"erpCode"})
    public ResponseEntity<Optional<LocationVO>> findLocationByErpCode(@RequestParam("erpCode") String erpCode) {
        Location location = locationService.findByErpCode(erpCode).orElseThrow(() -> locationNotFound(erpCode));
        return ResponseEntity.ok(Optional.ofNullable(mapper.map(location, LocationVO.class)));
    }

    @GetMapping(value = API_LOCATIONS, params = {"plcCode"})
    public ResponseEntity<Optional<LocationVO>> findLocationByPlcCode(@RequestParam("plcCode") String plcCode) {
        Location location = locationService.findByPlcCode(plcCode)
                .orElseThrow(() -> new NotFoundException(
                        translator,
                        LOCATION_NOT_FOUND_BY_PLC_CODE,
                        new String[]{plcCode},
                        plcCode
                ));
        return ResponseEntity.ok(Optional.ofNullable(mapper.map(location, LocationVO.class)));
    }

    @GetMapping(value = API_LOCATIONS, params = {"locationGroupNames"})
    public ResponseEntity<List<LocationVO>> findLocationsForLocationGroups(
            @RequestParam("locationGroupNames") List<String> locationGroupNames) {
        List<Location> locations = locationService.findAllOf(locationGroupNames);
        return ResponseEntity.ok(mapper.map(locations, LocationVO.class));
    }

    @PatchMapping(value = API_LOCATION + "/{pKey}", params = "op=change-state")
    public ResponseEntity<Void> changeState(
            @PathVariable(name = "pKey") String pKey,
            @RequestParam(name = "op") String op,
            @RequestBody ErrorCodeVO errorCode
    ) {
        locationService.changeState(pKey, errorCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Change the current {@code mode} a {@code Location}, identified by {@code erpCode}.
     *
     * @param erpCode The ERP code of the Location
     * @param type The type of lock to apply to the Location
     * @param mode The mode to apply to the Locations lock
     */
    @PostMapping(path = API_LOCATIONS , params = {"erpCode", "type!=PERMANENT_LOCK", "mode"})
    public ResponseEntity<Void> changeState(
            @RequestParam("erpCode") String erpCode,
            @RequestParam("type") LockType type,
            @RequestParam("mode") LockMode mode,
            @RequestParam(value = "plcState", required = false) Integer plcState
    ) {
        Location location = locationService.findByErpCode(erpCode).orElseThrow(() -> locationNotFound(erpCode));
        switch(type) {
            case ALLOCATION_LOCK:
                changeLocation(
                        mode,
                        location,
                        plcState,
                        (l, code) -> locationService.changeState(l.getPersistentKey(), code)
                );
                break;
            case OPERATION_LOCK:
                throw new UnsupportedOperationException("Changing the operation mode of Locations is currently not supported in the API");
            default:
                unsupportedOperation(type);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = API_LOCATIONS)
    public ResponseEntity<List<LocationVO>> findLocations(
            @RequestParam(value = "area", required = false, defaultValue = "%") String area,
            @RequestParam(value = "aisle", required = false, defaultValue = "%") String aisle,
            @RequestParam(value = "x", required = false, defaultValue = "%") String x,
            @RequestParam(value = "y", required = false, defaultValue = "%") String y,
            @RequestParam(value = "z", required = false, defaultValue = "%") String z
    ) {
        LocationPK pk = LocationPK.newBuilder()
                .area(area)
                .aisle(aisle)
                .x(x)
                .y(y)
                .z(z)
                .build();
        List<Location> locations = locationService.findLocations(pk);
        return locations.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(mapper.map(locations, LocationVO.class));
    }

    @GetMapping(API_LOCATIONS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(LocationController.class).changeState("pKey", "change-state", ErrorCodeVO.LOCK_STATE_IN_AND_OUT)).withRel("location-changestate"),
                        linkTo(methodOn(LocationController.class).createLocation(new LocationVO("locationId"), null)).withRel("location-create"),
                        linkTo(methodOn(LocationController.class).findLocationByCoordinate("AREA/AISLE/X/Y/Z")).withRel("location-findbycoordinate"),
                        linkTo(methodOn(LocationController.class).findLocationByErpCode("ERP_CODE")).withRel("location-findbyerpcode"),
                        linkTo(methodOn(LocationController.class).findLocationByPlcCode("PLC_CODE")).withRel("location-findbyplccode"),
                        linkTo(methodOn(LocationController.class).findLocations("area", "aisle", "x", "y", "z")).withRel("location-fortuple"),
                        linkTo(methodOn(LocationController.class).findLocationsForLocationGroups(asList("LG1", "LG2"))).withRel("location-forlocationgroup")
                )
        );
    }

    private void changeLocation(LockMode mode, Target target, Integer plcState, BiConsumer<Target, ErrorCodeVO> fnc) {
        ErrorCodeVO state;
        switch(mode) {
            case IN:
                state = ErrorCodeVO.LOCK_STATE_IN;
                state.setPlcState(plcState);
                fnc.accept(target, state);
                break;
            case OUT:
                state = ErrorCodeVO.LOCK_STATE_OUT;
                state.setPlcState(plcState);
                fnc.accept(target, state);
                break;
            case IN_AND_OUT:
                state = ErrorCodeVO.LOCK_STATE_IN_AND_OUT;
                state.setPlcState(plcState);
                fnc.accept(target, state);
                break;
            case NONE:
                state = ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT;
                state.setPlcState(plcState);
                fnc.accept(target, state);
                break;
            default:
                unsupportedOperation(mode);
        }
    }

    private NotFoundException locationNotFound(String erpCode) {
        return new NotFoundException(translator, LOCATION_NOT_FOUND_BY_ERP_CODE, new String[]{erpCode}, erpCode);
    }

    private void unsupportedOperation(LockMode mode) {
        throw new BusinessRuntimeException(translator, LOCK_MODE_UNSUPPORTED, new Serializable[]{mode}, mode);
    }

    private void unsupportedOperation(LockType type) {
        throw new BusinessRuntimeException(translator, LOCK_TYPE_UNSUPPORTED, new Serializable[]{type}, type);
    }
}
