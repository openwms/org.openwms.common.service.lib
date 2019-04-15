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
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.commands.MessageCommand;
import org.openwms.common.transport.commands.MessageCommandHandler;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.openwms.common.location.LocationPK.fromString;

/**
 * A TransportUnitController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!INMEM")
@RestController
public class TransportUnitController extends AbstractWebController {

    private final TransportUnitService service;
    private final BeanMapper mapper;
    private final MessageCommandHandler messageCommandHandler;

    TransportUnitController(TransportUnitService service, BeanMapper mapper, MessageCommandHandler messageCommandHandler) {
        this.service = service;
        this.mapper = mapper;
        this.messageCommandHandler = messageCommandHandler;
    }

    @GetMapping(value = "/v1/transport-units", params = {"bk"})
    @ResponseBody
    public TransportUnitVO findTransportUnit(@RequestParam("bk") String transportUnitBK, @RequestParam(value = "withErrors", required = false) Boolean withErrors) {
        TransportUnit transportUnit = service.findByBarcode(Barcode.of(transportUnitBK), withErrors);
        return mapper.map(transportUnit, TransportUnitVO.class);
    }

    @GetMapping(value = "/v1/transport-units", params = {"bks"})
    @ResponseBody
    public List<TransportUnitVO> findTransportUnits(@RequestParam("bks") List<String> barcodes) {
        List<TransportUnit> transportUnits = service.findByBarcodes(barcodes.stream().map(Barcode::of).collect(Collectors.toList()));
        return mapper.map(transportUnits, TransportUnitVO.class);
    }

    @GetMapping(value = "/v1/transport-units", params = {"actualLocation"})
    public List<TransportUnitVO> findTransportUnitsOn(@RequestParam("actualLocation") String actualLocation) {
        List<TransportUnit> tus = service.findOnLocation(actualLocation);
        return mapper.map(tus, TransportUnitVO.class);
    }

    @PostMapping(value = "/v1/transport-units", params = {"bk"})
    @ResponseBody
    public void createTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE == strict) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK), Boolean.FALSE);
        }
        TransportUnit toCreate = mapper.map(tu, TransportUnit.class);
        TransportUnit created = service.create(Barcode.of(transportUnitBK), toCreate.getTransportUnitType(), toCreate.getActualLocation().getLocationId(), strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
    }

    @PostMapping(value = "/v1/transport-units", params = {"bk", "actualLocation", "tut"})
    @ResponseBody
    public void createTU(@RequestParam("bk") String transportUnitBK, @RequestParam("actualLocation") String actualLocation, @RequestParam("tut") String tut, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE == strict) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK), Boolean.FALSE);
        }
        TransportUnit created = service.create(Barcode.of(transportUnitBK), tut, actualLocation, strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
    }

    @PutMapping(value = "/v1/transport-units", params = {"bk"})
    @ResponseBody
    public TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu) {
        return mapper.map(service.update(Barcode.of(transportUnitBK), mapper.map(tu, TransportUnit.class)), TransportUnitVO.class);
    }

    @PatchMapping(value = "/v1/transport-units", params = {"bk", "newLocation"})
    @ResponseBody
    public TransportUnitVO moveTU(@RequestParam("bk") String transportUnitBK, @RequestParam("newLocation") String newLocation) {
        TransportUnit tu = service.moveTransportUnit(Barcode.of(transportUnitBK), fromString(newLocation));
        return mapper.map(tu, TransportUnitVO.class);
    }

    @PostMapping(value = "/v1/transport-unit/error", params = {"bk", "errorCode"})
    public void addErrorToTransportUnit(@RequestParam("bk") String transportUnitBK, @RequestParam(value = "errorCode") String errorCode) {
        MessageCommand messageCommand = MessageCommand.newBuilder()
                .withType(MessageCommand.Type.ADD_TO_TU)
                .withTransportUnitId(transportUnitBK)
                .withMessageNumber(errorCode)
                .withMessageOccurred(new Date())
                .build();
        messageCommandHandler.handle(messageCommand);
    }
}
