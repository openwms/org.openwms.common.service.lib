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
package org.openwms.common.transport.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * A TransportUnitApi.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
//@FeignClient("${owms.common-service.protocol}://${owms.common-service.name}")
public interface TransportUnitApi {

    @GetMapping(params = {"bk"})
    @ResponseBody
    TransportUnitVO getTransportUnit(@RequestParam("bk") String transportUnitBK);

    @PostMapping(params = {"bk"})
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu, HttpServletRequest req);

    @PutMapping(params = {"bk"})
    @ResponseBody
    TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu);

    @PatchMapping(params = {"bk"})
    @ResponseBody
    TransportUnitVO updateActualLocation(@RequestParam("bk") String transportUnitBK, @RequestBody String actualLocation);
}
