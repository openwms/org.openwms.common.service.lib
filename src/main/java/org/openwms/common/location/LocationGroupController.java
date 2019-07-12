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
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.openwms.common.CommonConstants;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A LocationGroupController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!INMEM")
@RestController
public class LocationGroupController {

    private final LocationGroupService locationGroupService;
    private final BeanMapper mapper;
    private final ErrorCodeTransformers.GroupStateIn groupStateIn;
    private final ErrorCodeTransformers.GroupStateOut groupStateOut;

    LocationGroupController(LocationGroupService locationGroupService, BeanMapper mapper, ErrorCodeTransformers.GroupStateIn groupStateIn, ErrorCodeTransformers.GroupStateOut groupStateOut) {
        this.locationGroupService = locationGroupService;
        this.mapper = mapper;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"name"})
    public LocationGroupVO findByName(@RequestParam("name") String name) {
        Optional<LocationGroup> opt = locationGroupService.findByName(name);
        LocationGroup locationGroup = opt.orElseThrow(() -> new NotFoundException(format("LocationGroup with name [%s] does not exist", name)));
        LocationGroupVO result = mapper.map(locationGroup, LocationGroupVO.class);
        if (locationGroup.hasParent()) {
            result.add(linkTo(methodOn(LocationGroupController.class).findByName(locationGroup.getParent().getName())).withRel("_parent"));
        }
        return result;
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"names"})
    public List<LocationGroupVO> findByNames(@RequestParam("names") List<String> names) {
        List<LocationGroup> locationGroups = locationGroupService.findByNames(names);
        return mapper.map(locationGroups, LocationGroupVO.class);
    }

    @GetMapping(value = CommonConstants.API_LOCATION_GROUPS)
    public List<LocationGroupVO> findAll() {
        List<LocationGroup> all = locationGroupService.findAll();
        return all == null ? Collections.emptyList() : mapper.map(all, LocationGroupVO.class);
    }

    @PatchMapping(value = CommonConstants.API_LOCATION_GROUPS, params = {"name"})
    public void updateState(@RequestParam(name = "name") String name, @RequestBody ErrorCodeVO errorCode) {
        locationGroupService.changeGroupStates(
                name,
                groupStateIn.available(errorCode.getErrorCode()),
                groupStateOut.available(errorCode.getErrorCode())
        );
    }

    @PatchMapping(value = CommonConstants.API_LOCATION_GROUPS + "/{pKey}")
    public void save(
            @PathVariable String pKey,
            @RequestParam(name = "statein", required = false) LocationGroupState stateIn,
            @RequestParam(name = "stateout", required = false) LocationGroupState stateOut,
            HttpServletRequest req, HttpServletResponse res) {
        locationGroupService.changeGroupState(pKey, stateIn, stateOut);
        res.addHeader(HttpHeaders.LOCATION, getLocationForCreatedResource(req, pKey));
    }

    private String getLocationForCreatedResource(HttpServletRequest req, String objId) {
        return new UriTemplate(req.getRequestURL().append("/{objId}/")
                .toString())
                .expand(objId)
                .toASCIIString();
    }
}
