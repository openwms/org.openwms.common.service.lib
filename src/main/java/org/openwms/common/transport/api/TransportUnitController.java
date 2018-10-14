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

import org.ameba.mapping.BeanMapper;
import org.openwms.common.CommonConstants;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.core.http.AbstractWebController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * A TransportUnitController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @since 2.0
 */
@RestController(CommonConstants.API_TRANSPORTUNITS)
public class TransportUnitController extends AbstractWebController implements TransportUnitApi {

    @Autowired
    private TransportUnitService<TransportUnit> service;
    @Autowired
    private BeanMapper mapper;

    @Override
    @GetMapping(params = {"bk"})
    public
    @ResponseBody
    TransportUnitVO getTransportUnit(@RequestParam("bk") String transportUnitBK) {
        TransportUnit transportUnit = service.findByBarcode(new Barcode(transportUnitBK));
        return mapper.map(transportUnit, TransportUnitVO.class);
    }

    @Override
    @PostMapping(params = {"bk"})
    public
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE == strict) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK));
        }
        TransportUnit toCreate = mapper.map(tu, TransportUnit.class);
        TransportUnit created = service.create(new Barcode(transportUnitBK), toCreate.getTransportUnitType(), toCreate.getActualLocation().getLocationId(), strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
    }

    @PostMapping(params = {"bk", "actualLocation", "tut"})
    public
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestParam("actualLocation") String actualLocation, @RequestParam("tut") String tut, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE == strict) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK));
        }
        TransportUnit created = service.create(new Barcode(transportUnitBK), tut, actualLocation, strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
    }

    @Override
    @PutMapping(params = {"bk"})
    public
    @ResponseBody
    TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu) {
        return mapper.map(service.update(new Barcode(transportUnitBK), mapper.map(tu, TransportUnit.class)), TransportUnitVO.class);
    }

    @Override
    @PatchMapping(params = {"bk"})
    public
    @ResponseBody
    TransportUnitVO updateActualLocation(@RequestParam("bk") String transportUnitBK, @RequestBody String actualLocation) {
        System.out.println("Location updated");
        return null;
    }
}
