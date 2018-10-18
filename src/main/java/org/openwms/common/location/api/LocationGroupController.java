/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.location.api;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.openwms.common.CommonConstants;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupService;
import org.openwms.common.location.LocationGroupState;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A LocationGroupController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@RestController(CommonConstants.API_LOCATIONGROUPS)
class LocationGroupController implements LocationGroupApi {

    private final LocationGroupService<LocationGroup> locationGroupService;
    private final Translator translator;
    private final BeanMapper mapper;
    private final ErrorCodeTransformers.GroupStateIn groupStateIn;
    private final ErrorCodeTransformers.GroupStateOut groupStateOut;

    LocationGroupController(LocationGroupService<LocationGroup> locationGroupService, Translator translator, BeanMapper mapper, ErrorCodeTransformers.GroupStateIn groupStateIn, ErrorCodeTransformers.GroupStateOut groupStateOut) {
        this.locationGroupService = locationGroupService;
        this.translator = translator;
        this.mapper = mapper;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }


    @Override
    @PatchMapping(value = CommonConstants.API_LOCATIONGROUPS + "/{id}")
    public void save(@PathVariable String id, @RequestParam(name = "statein", required = false) LocationGroupState stateIn, @RequestParam(name = "stateout", required = false) LocationGroupState stateOut, HttpServletRequest req, HttpServletResponse res) {
        locationGroupService.changeGroupState(id, stateIn, stateOut);
        res.addHeader(HttpHeaders.LOCATION, getLocationForCreatedResource(req, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping(value = CommonConstants.API_LOCATIONGROUPS, params = {"name"})
    public void updateState(@RequestParam(name = "name") String locationGroupName, @RequestBody ErrorCodeVO errorCode, HttpServletRequest req, HttpServletResponse res) {
        locationGroupService.changeGroupStates(locationGroupName, groupStateIn.transform(errorCode.errorCode), groupStateOut.transform(errorCode.errorCode));
    }

    @Override
    @RequestMapping(value = CommonConstants.API_LOCATIONGROUPS, method = RequestMethod.GET, params = {"name"})
    public LocationGroupVO getLocationGroup(@RequestParam("name") String name) {
        Optional<LocationGroup> opt = locationGroupService.findByName(name);
        LocationGroup locationGroup = opt.orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.LOCATION_GROUP_NOT_FOUND, new String[]{name}, name));
        LocationGroupVO result = mapper.map(locationGroup, LocationGroupVO.class);
        if (locationGroup.hasParent()) {
            result.add(linkTo(methodOn(LocationGroupController.class).getLocationGroup(locationGroup.getParent().getName())).withRel("_parent"));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationGroupVO> findAll() {
        List<LocationGroup> all = locationGroupService.findAll();
        return all == null ? Collections.emptyList() : mapper.map(all, LocationGroupVO.class);
    }

    @Override
    public String getLocationForCreatedResource(HttpServletRequest req, String objId) {
        StringBuffer url = req.getRequestURL();
        UriTemplate template = new UriTemplate(url.append("/{objId}/").toString());
        return template.expand(objId).toASCIIString();
    }
}
