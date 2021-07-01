/*
 * Copyright 2005-2020 the original author or authors.
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

import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.SimpleLink;
import org.openwms.common.StateChangeException;
import org.openwms.common.location.LocationController;
import org.openwms.common.transport.api.TransportApiConstants;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.ValidationGroups;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.openwms.common.CommonMessageCodes.BARCODE_MISSING;
import static org.openwms.common.CommonMessageCodes.TRANSPORT_UNIT_EXISTS;
import static org.openwms.common.location.LocationPK.fromString;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNITS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A TransportUnitController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@MeasuredRestController
public class TransportUnitController extends AbstractWebController {

    private final BeanMapper mapper;
    private final Translator translator;
    private final BarcodeGenerator barcodeGenerator;
    private final TransportUnitService service;

    TransportUnitController(BeanMapper mapper, Translator translator, BarcodeGenerator barcodeGenerator, TransportUnitService service) {
        this.mapper = mapper;
        this.translator = translator;
        this.barcodeGenerator = barcodeGenerator;
        this.service = service;
    }

    @GetMapping(value = API_TRANSPORT_UNITS + "/{pKey}", produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<TransportUnitVO> findTransportUnitByPKey(
            @PathVariable("pKey") String pKey
    ) {
        TransportUnit transportUnit = service.findByPKey(pKey);
        TransportUnitVO result = mapper.map(transportUnit, TransportUnitVO.class);
        addLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<TransportUnitVO> findTransportUnit(
            @RequestParam("bk") String transportUnitBK
    ) {
        TransportUnit transportUnit = service.findByBarcode(transportUnitBK);
        TransportUnitVO result = mapper.map(transportUnit, TransportUnitVO.class);
        addLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = API_TRANSPORT_UNITS, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<Page<TransportUnitVO>> findAll() {
        List<TransportUnit> transportUnits = service.findAll();
        List<TransportUnitVO> result = mapper.map(transportUnits, TransportUnitVO.class);
        return ResponseEntity.ok(new PageImpl<>(result));
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

    /*
     * Find all TransportUnits by their business keys.
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bks"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<List<TransportUnitVO>> findTransportUnits(
            @RequestParam("bks") List<String> barcodes
    ) {
        List<TransportUnit> tus = service.findByBarcodes(barcodes.stream().map(barcodeGenerator::convert).collect(Collectors.toList()));
        return ResponseEntity.ok(augmentResults(tus));
    }

    /*
     * Find all TransportUnits placed on the given Location.
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<List<TransportUnitVO>> findTransportUnitsOn(
            @RequestParam("actualLocation") String actualLocation
    ) {
        List<TransportUnit> tus = service.findOnLocation(actualLocation);
        return tus == null ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(augmentResults(tus));
    }

    private List<TransportUnitVO> augmentResults(List<TransportUnit> tus) {
        List<TransportUnitVO> result = mapper.map(tus, TransportUnitVO.class);
        result.forEach(this::addLinks);
        return result;
    }

    /*
     * Create a TransportUnit with values in the Body.
     */
    @PostMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    public ResponseEntity<Void> createTU(
            @RequestParam("bk") String transportUnitBK,
            @Validated(ValidationGroups.TransportUnit.Create.class) @RequestBody TransportUnitVO tu,
            @RequestParam(value = "strict", required = false) Boolean strict,
            HttpServletRequest req
    ) {
        if (Boolean.TRUE.equals(strict)) {
            // check if already exists ...
            try {
                service.findByBarcode(transportUnitBK);
                throw new ResourceExistsException(translator.translate(TRANSPORT_UNIT_EXISTS, transportUnitBK), TRANSPORT_UNIT_EXISTS, transportUnitBK);
            } catch (NotFoundException nfe) {
                // thats fine we just cast the exception thrown by the service
            }
        }
        TransportUnit created = service.create(transportUnitBK, tu.getTransportUnitType(), tu.getActualLocation().getLocationId(), strict);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, created.getPersistentKey())).build();
    }

    @PostMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation", "tut"})
    public ResponseEntity<TransportUnitVO> createTU(
            @RequestParam(value = "bk", required = false) String transportUnitBK,
            @RequestParam("actualLocation") String actualLocation,
            @RequestParam("tut") String tut,
            @RequestParam(value = "strict", required = false) Boolean strict,
            HttpServletRequest req
    ) {
        if (Boolean.TRUE.equals(strict)) {

            if (transportUnitBK == null || transportUnitBK.isEmpty()) {
                throw new IllegalArgumentException(translator.translate(BARCODE_MISSING));
            }

            // check if already exists ...
            try {
                service.findByBarcode(transportUnitBK);
                throw new ResourceExistsException(translator.translate(TRANSPORT_UNIT_EXISTS, transportUnitBK), TRANSPORT_UNIT_EXISTS, transportUnitBK);
            } catch (NotFoundException nfe) {
                // that's fine we just cast the exception thrown by the service
            }
        }
        TransportUnit created = transportUnitBK == null
                ? service.createNew(tut, actualLocation)
                : service.create(transportUnitBK, tut, actualLocation, strict);
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, created.getPersistentKey()))
                .body(mapper.map(created, TransportUnitVO.class))
                ;
    }

    /*
     * Update a TransportUnits data.
     */
    @PutMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    public ResponseEntity<TransportUnitVO> updateTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestBody TransportUnitVO tu
    ) {
        return ResponseEntity.ok(
                mapper.map(service.update(barcodeGenerator.convert(transportUnitBK), mapper.map(tu, TransportUnit.class)), TransportUnitVO.class)
        );
    }

    /*
     * Move an existing TransportUnit to a new Location.
     */
    @PatchMapping(value = API_TRANSPORT_UNITS, params = {"bk", "newLocation"})
    public ResponseEntity<TransportUnitVO> moveTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("newLocation") String newLocation
    ) {
        TransportUnit tu = service.moveTransportUnit(barcodeGenerator.convert(transportUnitBK), fromString(newLocation));
        return ResponseEntity.ok(
                mapper.map(tu, TransportUnitVO.class)
        );
    }

    /*
     * Write an error message to an existing TransportUnit.
     */
    @PostMapping(value = API_TRANSPORT_UNIT + "/error", params = {"bk", "errorCode"})
    public ResponseEntity<Void> addErrorToTransportUnit(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam(value = "errorCode") String errorCode
    ) {
        service.addError(transportUnitBK, UnitError.newBuilder()
                .errorNo(errorCode)
                .build()
        );
        return ResponseEntity.ok().build();
    }

    /*
     * The index.
     */
    @GetMapping(API_TRANSPORT_UNITS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitByPKey("1")).withRel("transport-unit-findbypkey"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnit("00000000000000004711")).withRel("transport-unit-findbybarcode"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnits(asList("00000000000000004711", "00000000000000004712"))).withRel("transport-unit-findbybarcodes"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitsOn("EXT_/0000/0000/0000/0000")).withRel("transport-unit-findonlocation")
                )
        );
    }

    /**
     * Set the state of a {@code TransportUnit} to BLOCKED.
     *
     * @param transportUnitBK The unique (physical) identifier
     */
    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNIT + "/block", params = {"bk"})
    public ResponseEntity<Void> blockTransportUnit(@RequestParam("bk") String transportUnitBK) {
        try {
            service.setState(transportUnitBK, TransportUnitState.BLOCKED);
            return ResponseEntity.ok().build();
        } catch (StateChangeException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }
    }

    /**
     * Set the state of a {@code TransportUnit} to AVAILABLE.
     *
     * @param transportUnitBK The unique (physical) identifier
     */
    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNIT + "/available", params = {"bk"})
    public ResponseEntity<Void> unblockTransportUnit(@RequestParam("bk") String transportUnitBK) {
        try {
            service.setState(transportUnitBK, TransportUnitState.BLOCKED);
            return ResponseEntity.ok().build();
        } catch (StateChangeException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }
    }

    /**
     * Set the state of a {@code TransportUnit} to QUALITY_CHECK.
     *
     * @param transportUnitBK The unique (physical) identifier
     */
    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNIT + "/quality-check", params = {"bk"})
    public ResponseEntity<Void> qcTransportUnit(@RequestParam("bk") String transportUnitBK) {
        try {
            service.setState(transportUnitBK, TransportUnitState.BLOCKED);
            return ResponseEntity.ok().build();
        } catch (StateChangeException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }
    }

}
