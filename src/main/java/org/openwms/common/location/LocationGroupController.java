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
import org.openwms.common.SimpleLink;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A LocationGroupController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@RestController
public class LocationGroupController extends AbstractWebController {

    public static final String PARENT = "parent";
    private final LocationGroupService locationGroupService;
    private final BeanMapper mapper;
    private final ErrorCodeTransformers.GroupStateIn groupStateIn;
    private final ErrorCodeTransformers.GroupStateOut groupStateOut;

    LocationGroupController(LocationGroupService locationGroupService, BeanMapper mapper, ErrorCodeTransformers.GroupStateIn groupStateIn,
            ErrorCodeTransformers.GroupStateOut groupStateOut) {
        this.locationGroupService = locationGroupService;
        this.mapper = mapper;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"name"})
    public LocationGroupVO findByName(@RequestParam("name") String name) {
        LocationGroup locationGroup = locationGroupService.findByName(name)
                .orElseThrow(() -> new NotFoundException(format("LocationGroup with name [%s] does not exist", name)));
        LocationGroupVO result = mapper.map(locationGroup, LocationGroupVO.class);
        if (locationGroup.hasParent()) {
            result.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class).findByName(locationGroup.getParent().getName())).withRel(PARENT)));
        }
        return result;
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"names"})
    public List<LocationGroupVO> findByNames(@RequestParam("names") List<String> names) {
        List<LocationGroup> locationGroups = locationGroupService.findByNames(names);
        List<LocationGroupVO> vos = mapper.map(locationGroups, LocationGroupVO.class);
        vos.forEach(lg -> {
            if (lg.hasParent()) {
                lg.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class).findByName(lg.getParent())).withRel(PARENT)));
            }
        });
        return vos;
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS)
    public List<LocationGroupVO> findAll() {
        List<LocationGroup> all = locationGroupService.findAll();
        List<LocationGroupVO> result = all == null ? Collections.emptyList() : mapper.map(all, LocationGroupVO.class);
        result.forEach(lg -> {
                    if (lg.hasParent()) {
                        lg.add(new SimpleLink(linkTo(methodOn(LocationGroupController.class).findByName(lg.getParent())).withRel(PARENT)));
                    }
                }
        );
        return result;
    }

    @PatchMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"name"})
    public void changeGroupState(
            @RequestParam(name = "name") String name,
            @RequestBody ErrorCodeVO errorCode) {
        locationGroupService.changeGroupStates(
                name,
                groupStateIn.available(errorCode.getErrorCode()),
                groupStateOut.available(errorCode.getErrorCode())
        );
    }

    @PatchMapping(value = CommonConstants.API_LOCATION_GROUPS + "/{pKey}")
    public void changeGroupState(
            @PathVariable String pKey,
            @RequestParam(name = "statein") LocationGroupState stateIn,
            @RequestParam(name = "stateout") LocationGroupState stateOut) {
        locationGroupService.changeGroupState(pKey, stateIn, stateOut);
    }

    @GetMapping(CommonConstants.API_LOCATION_GROUPS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(LocationGroupController.class).findAll()).withRel("location-group-findall"),
                        linkTo(methodOn(LocationGroupController.class).findByName("FOO")).withRel("location-group-findbyname"),
                        linkTo(methodOn(LocationGroupController.class).findByNames(asList("FOO", "BAR"))).withRel("location-group-findbynames")
                )
        );
    }
}
