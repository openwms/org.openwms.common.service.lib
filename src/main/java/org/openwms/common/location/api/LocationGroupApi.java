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

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.openwms.common.CommonConstants;
import org.openwms.common.location.LocationGroupState;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * A LocationGroupApi.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@FeignClient("${owms.common-service.protocol}://${owms.common-service.name}")
public interface LocationGroupApi {

    @PatchMapping(value = CommonConstants.API_LOCATIONGROUPS + "/{id}")
    void save(@PathVariable String id, @RequestParam(name = "statein", required = false) LocationGroupState stateIn, @RequestParam(name = "stateout", required = false) LocationGroupState stateOut, HttpServletRequest req, HttpServletResponse res);

    /**
     * This method is used to update the state of a LocationGroup with an errorCode String, usually coming from a SYSU telegram.
     *
     * @param locationGroupName The name of the LocationGroup
     * @param errorCode The error code as String
     * @param req HttpRequest
     * @param res HttpResponse
     */
    @PatchMapping(value = CommonConstants.API_LOCATIONGROUPS, params = {"name"})
    void updateState(@RequestParam(name = "name") String locationGroupName, @RequestBody ErrorCodeVO errorCode, HttpServletRequest req, HttpServletResponse res);

    @RequestMapping(value = CommonConstants.API_LOCATIONGROUPS, method = RequestMethod.GET, params = {"name"})
    LocationGroupVO getLocationGroup(@RequestParam("name") String name);

    String getLocationForCreatedResource(HttpServletRequest req, String objId);
}
