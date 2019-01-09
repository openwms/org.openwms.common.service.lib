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
package org.openwms.common.transport;

import org.ameba.mapping.BeanMapper;
import org.openwms.common.CommonConstants;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openwms.common.location.LocationPK.fromString;

/**
 * A TransportUnitController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!INMEM")
@RestController
class TransportUnitController extends AbstractWebController {

    private final TransportUnitService<TransportUnit> service;
    private final BeanMapper mapper;

    TransportUnitController(TransportUnitService<TransportUnit> service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"bk"})
    @ResponseBody
    TransportUnitVO findTransportUnit(@RequestParam("bk") String transportUnitBK) {
        TransportUnit transportUnit = service.findByBarcode(new Barcode(transportUnitBK));
        return mapper.map(transportUnit, TransportUnitVO.class);
    }

    @GetMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"actualLocation"})
    List<TransportUnitVO> getTransportUnitsOn(@RequestParam("actualLocation") String actualLocation) {
        List<TransportUnit> tus = service.findOnLocation(actualLocation);
        return mapper.map(tus, TransportUnitVO.class);
    }

    @PostMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"bk"})
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

    @PostMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"bk", "actualLocation", "tut"})
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestParam("actualLocation") String actualLocation, @RequestParam("tut") String tut, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE == strict) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK));
        }
        TransportUnit created = service.create(new Barcode(transportUnitBK), tut, actualLocation, strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
    }

    @PutMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"bk"})
    @ResponseBody
    TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu) {
        return mapper.map(service.update(new Barcode(transportUnitBK), mapper.map(tu, TransportUnit.class)), TransportUnitVO.class);
    }

    @PatchMapping(value = CommonConstants.API_TRANSPORT_UNITS, params = {"bk", "newLocation"})
    @ResponseBody
    TransportUnitVO moveTU(@RequestParam("bk") String transportUnitBK, @RequestParam("newLocation") String newLocation) {
        TransportUnit tu = service.moveTransportUnit(new Barcode(transportUnitBK), fromString(newLocation));
        return mapper.map(tu, TransportUnitVO.class);
    }
}
