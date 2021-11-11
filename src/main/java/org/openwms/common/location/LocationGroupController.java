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

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.SimpleLink;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.openwms.common.CommonMessageCodes.LOCATION_GROUP_NOT_FOUND;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION_GROUP;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION_GROUPS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A LocationGroupController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@RefreshScope
@MeasuredRestController
public class LocationGroupController extends AbstractWebController {

    public static final String PARENT = "parent";
    private final Translator translator;
    private final LocationGroupService locationGroupService;
    private final BeanMapper mapper;
    private final ErrorCodeTransformers.GroupStateIn groupStateIn;
    private final ErrorCodeTransformers.GroupStateOut groupStateOut;

    LocationGroupController(Translator translator, LocationGroupService locationGroupService, BeanMapper mapper,
                            ErrorCodeTransformers.GroupStateIn groupStateIn,
                            ErrorCodeTransformers.GroupStateOut groupStateOut) {
        this.translator = translator;
        this.locationGroupService = locationGroupService;
        this.mapper = mapper;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }

    @Transactional(readOnly = true)
    @GetMapping(value = API_LOCATION_GROUPS, params = {"name"})
    public LocationGroupVO findByName(
            @RequestParam("name") String name
    ) {
        LocationGroup locationGroup = locationGroupService.findByName(name)
                .orElseThrow(() -> new NotFoundException(translator, LOCATION_GROUP_NOT_FOUND, new String[]{name}, name));
        LocationGroupVO result = mapper.map(locationGroup, LocationGroupVO.class);
        if (locationGroup.hasParent()) {
            result.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class)
                    .findByName(locationGroup.getParent().getName())).withRel(PARENT)));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @GetMapping(value = API_LOCATION_GROUPS, params = {"names"})
    public List<LocationGroupVO> findByNames(
            @RequestParam("names") List<String> names
    ) {
        List<LocationGroup> locationGroups = locationGroupService.findByNames(names);
        List<LocationGroupVO> vos = mapper.map(locationGroups, LocationGroupVO.class);
        vos.forEach(lg -> {
            if (lg.hasParent()) {
                lg.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class)
                        .findByName(lg.getParent())).withRel(PARENT)));
            }
        });
        return vos;
    }

    @Transactional(readOnly = true)
    @GetMapping(API_LOCATION_GROUPS)
    public List<LocationGroupVO> findAll() {
        List<LocationGroup> all = locationGroupService.findAll();
        List<LocationGroupVO> result = all == null ? Collections.emptyList() : mapper.map(all, LocationGroupVO.class);
        result.forEach(lg -> {
                    if (lg.hasParent()) {
                        lg.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class)
                                .findByName(lg.getParent())).withRel(PARENT)));
                    }
                }
        );
        return result;
    }

    @PatchMapping(value = API_LOCATION_GROUPS, params = {"name", "op=change-state"})
    public ResponseEntity<Void> changeGroupState(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "op") String op,
            @RequestBody ErrorCodeVO errorCode
    ) {
        locationGroupService.changeGroupStates(
                name,
                groupStateIn.available(errorCode.getErrorCode()),
                groupStateOut.available(errorCode.getErrorCode())
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = API_LOCATION_GROUP + "/{pKey}", params = "op=change-state")
    public ResponseEntity<Void> changeGroupState(
            @PathVariable String pKey,
            @RequestParam(name = "op") String op,
            @RequestParam(name = "statein") LocationGroupState stateIn,
            @RequestParam(name = "stateout") LocationGroupState stateOut
    ) {
        locationGroupService.changeGroupState(pKey, stateIn, stateOut);
        return ResponseEntity.ok().build();
    }

    @GetMapping(API_LOCATION_GROUPS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(LocationGroupController.class).findAll()).withRel("location-groups-findall"),
                        linkTo(methodOn(LocationGroupController.class).findByName("FOO")).withRel("location-groups-findbyname"),
                        linkTo(methodOn(LocationGroupController.class).findByNames(asList("FOO", "BAR"))).withRel("location-groups-findbynames"),
                        linkTo(methodOn(LocationGroupController.class).changeGroupState("FOO", "change-state", ErrorCodeVO.LOCK_STATE_IN_AND_OUT)).withRel("location-groups-changestate1"),
                        linkTo(methodOn(LocationGroupController.class).changeGroupState("UUID", "change-state", LocationGroupState.AVAILABLE, LocationGroupState.NOT_AVAILABLE)).withRel("location-groups-changestate2")
                )
        );
    }
}
