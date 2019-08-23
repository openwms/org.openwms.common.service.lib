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
package org.openwms.common.transport;

import org.ameba.mapping.BeanMapper;
import org.openwms.common.Index;
import org.openwms.common.SimpleLink;
import org.openwms.common.location.LocationController;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.commands.MessageCommand;
import org.openwms.common.transport.commands.MessageCommandHandler;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.openwms.common.CommonConstants.API_TRANSPORT_UNITS;
import static org.openwms.common.location.LocationPK.fromString;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A TransportUnitController.
 *
 * @author Heiko Scherrer
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

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<TransportUnitVO> findTransportUnit(@RequestParam("bk") String transportUnitBK) {
        TransportUnit transportUnit = service.findByBarcode(Barcode.of(transportUnitBK));
        TransportUnitVO result = mapper.map(transportUnit, TransportUnitVO.class);
        addLinks(result);
        return ResponseEntity.ok(result);
    }

    private void addLinks(TransportUnitVO result) {
        result.add(
                new SimpleLink(linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitType(result.getTransportUnitType())).withRel("transport-unit-type"))
        );
        if (result.getActualLocation() != null) {
            result.add(
                    new SimpleLink(linkTo(methodOn(LocationController.class).findLocationByCoordinate(result.getActualLocation().getLocationId())).withRel("actual-location"))
            );
        }
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bks"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<List<TransportUnitVO>> findTransportUnits(@RequestParam("bks") List<String> barcodes) {
        List<TransportUnit> tus = service.findByBarcodes(barcodes.stream().map(Barcode::of).collect(Collectors.toList()));
        return ResponseEntity.ok(augmentResults(tus));
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<List<TransportUnitVO>> findTransportUnitsOn(@RequestParam("actualLocation") String actualLocation) {
        List<TransportUnit> tus = service.findOnLocation(actualLocation);
        return tus == null ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(augmentResults(tus));
    }

    private List<TransportUnitVO> augmentResults(List<TransportUnit> tus) {
        List<TransportUnitVO> result = mapper.map(tus, TransportUnitVO.class);
        result.forEach(this::addLinks);
        return result;
    }

    @PostMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    public ResponseEntity<Void> createTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE.equals(strict)) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK));
        }
        TransportUnit toCreate = mapper.map(tu, TransportUnit.class);
        TransportUnit created = service.create(Barcode.of(transportUnitBK), toCreate.getTransportUnitType(), toCreate.getActualLocation().getLocationId(), strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = API_TRANSPORT_UNITS, params = {"bk", "actualLocation", "tut"})
    public ResponseEntity<Void> createTU(@RequestParam("bk") String transportUnitBK, @RequestParam("actualLocation") String actualLocation, @RequestParam("tut") String tut, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req) {
        if (Boolean.TRUE.equals(strict)) {
            // check if already exists ...
            service.findByBarcode(Barcode.of(transportUnitBK));
        }
        TransportUnit created = service.create(Barcode.of(transportUnitBK), tut, actualLocation, strict);
        getLocationForCreatedResource(req, created.getPersistentKey());
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    public TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu) {
        return mapper.map(service.update(Barcode.of(transportUnitBK), mapper.map(tu, TransportUnit.class)), TransportUnitVO.class);
    }

    @PatchMapping(value = API_TRANSPORT_UNITS, params = {"bk", "newLocation"})
    public TransportUnitVO moveTU(@RequestParam("bk") String transportUnitBK, @RequestParam("newLocation") String newLocation) {
        TransportUnit tu = service.moveTransportUnit(Barcode.of(transportUnitBK), fromString(newLocation));
        return mapper.map(tu, TransportUnitVO.class);
    }

    @PostMapping(value = API_TRANSPORT_UNITS + "/error", params = {"bk", "errorCode"})
    public void addErrorToTransportUnit(@RequestParam("bk") String transportUnitBK, @RequestParam(value = "errorCode") String errorCode) {
        MessageCommand messageCommand = MessageCommand.newBuilder()
                .withType(MessageCommand.Type.ADD_TO_TU)
                .withTransportUnitId(transportUnitBK)
                .withMessageNumber(errorCode)
                .withMessageOccurred(new Date())
                .build();
        messageCommandHandler.handle(messageCommand);
    }

    @GetMapping(API_TRANSPORT_UNITS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(TransportUnitController.class).findTransportUnit("00000000000000004711")).withRel("transport-unit-findbybarcode"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnits(asList("00000000000000004711", "00000000000000004712"))).withRel("transport-unit-findbybarcodes"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitsOn("EXT_/0000/0000/0000/0000")).withRel("transport-unit-findonlocation")
                )
        );
    }
}
