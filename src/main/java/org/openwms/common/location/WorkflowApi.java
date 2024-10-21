/*
 * Copyright 2005-2024 the original author or authors.
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.location.api.ValidationGroups;
import org.openwms.core.SpringProfiles;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static org.openwms.common.CommonMessageCodes.LOCATION_ID_INVALID;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATIONS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A WorkflowApi.
 *
 * @author Heiko Scherrer
 */
@Profile("!" + SpringProfiles.IN_MEMORY)
@Validated
@MeasuredRestController
public class WorkflowApi extends AbstractWebController {

    private final LocationMapper mapper;
    private final Translator translator;
    private final LocationService locationService;

    WorkflowApi(LocationService locationService, LocationMapper mapper, Translator translator) {
        this.locationService = locationService;
        this.mapper = mapper;
        this.translator = translator;
    }

    @PostMapping(value = API_LOCATIONS, produces = LocationVO.MEDIA_TYPE_OPT)
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<Optional<LocationVO>> createLocation(@Valid @RequestBody LocationVO location, HttpServletRequest req) {
        try {
            var created = locationService.create(mapper.convertVO(location));
            var result = mapper.convertToVO(created);
            addSelfLink(result);
            return ResponseEntity
                    .created(super.getLocationURIForCreatedResource(req, created.getPersistentKey()))
                    .body(Optional.of(result));
        } catch (ResourceExistsException e) {
            return ResponseEntity.ok(Optional.empty());
        }
    }

    @PutMapping(value = API_LOCATIONS, produces = LocationVO.MEDIA_TYPE_OPT)
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<Optional<LocationVO>> updateLocation(@Valid @RequestBody LocationVO location) {
        try {
            var updated = locationService.save(mapper.convertVO(location));
            var result = mapper.convertToVO(updated);
            addSelfLink(result);
            return ResponseEntity.ok(Optional.of(result));
        } catch (NotFoundException e) {
            return ResponseEntity.ok(Optional.empty());
        }
    }

    @GetMapping(value = API_LOCATIONS + "/{pKey}", produces = LocationVO.MEDIA_TYPE_OPT)
    public ResponseEntity<Optional<LocationVO>> findByPKeyOpt(@PathVariable("pKey") String pKey) {
        try {
            var location = locationService.findByPKey(pKey);
            var result = mapper.convertToVO(location);
            addSelfLink(result);
            return ResponseEntity.ok(Optional.of(result));
        } catch (NotFoundException e) {
            return ResponseEntity.ok(Optional.empty());
        }
    }

    private void addSelfLink(LocationVO result) {
        result.add(linkTo(methodOn(WorkflowApi.class).findByPKeyOpt(result.getpKey())).withRel("location-findbypkey"));
    }

    @GetMapping(value = API_LOCATIONS, params = {"locationId"}, produces = LocationVO.MEDIA_TYPE_OPT)
    public ResponseEntity<Optional<LocationVO>> findByIdOpt(@RequestParam("locationId") String locationId) {
        if (!LocationPK.isValid(locationId)) {
            // here we need to throw an NFE because Feign needs to cast it into an Optional. IAE won't work!
            throw new NotFoundException(translator, LOCATION_ID_INVALID, new String[]{locationId}, locationId);
        }
        var locationOpt = locationService.findByLocationPk(LocationPK.fromString(locationId));
        if (locationOpt.isPresent()) {
            var result = mapper.convertToVO(locationOpt.get());
            addSelfLink(result);
            return ResponseEntity.ok(Optional.of(result));
        }
        return ResponseEntity.ok(Optional.empty());
    }

    @GetMapping(value = API_LOCATIONS, params = {"erpCode"}, produces = LocationVO.MEDIA_TYPE_OPT)
    public ResponseEntity<Optional<LocationVO>> findByErpCodeOpt(@RequestParam("erpCode") String erpCode) {
        var locationOpt = locationService.findByErpCode(erpCode);
        if (locationOpt.isPresent()) {
            var result = mapper.convertToVO(locationOpt.get());
            addSelfLink(result);
            return ResponseEntity.ok(Optional.of(result));
        }
        return ResponseEntity.ok(Optional.empty());
    }

    @GetMapping(value = API_LOCATIONS, params = {"plcCode"}, produces = LocationVO.MEDIA_TYPE_OPT)
    public ResponseEntity<Optional<LocationVO>> findByPlcCodeOpt(@RequestParam("plcCode") String plcCode) {
        var locationOpt = locationService.findByPlcCode(plcCode);
        if (locationOpt.isPresent()) {
            var result = mapper.convertToVO(locationOpt.get());
            addSelfLink(result);
            return ResponseEntity.ok(Optional.of(result));
        }
        return ResponseEntity.ok(Optional.empty());
    }
}
