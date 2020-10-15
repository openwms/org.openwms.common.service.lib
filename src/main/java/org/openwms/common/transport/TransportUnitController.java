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
import org.openwms.common.location.LocationController;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.ValidationGroups;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
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
            @PathVariable("pKey") String pKey,  @RequestHeader Map<String, String> headers
    ) {
        TransportUnit transportUnit = service.findByPKey(pKey);
        TransportUnitVO result = mapper.map(transportUnit, TransportUnitVO.class);
        addLinks(result, headers);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<TransportUnitVO> findTransportUnit(
            @RequestParam("bk") String transportUnitBK,  @RequestHeader Map<String, String> headers
    ) {
        TransportUnit transportUnit = service.findByBarcode(barcodeGenerator.convert(transportUnitBK));
        TransportUnitVO result = mapper.map(transportUnit, TransportUnitVO.class);
        addLinks(result, headers);
        return ResponseEntity.ok(result);
    }

    private void addLinks(TransportUnitVO result, Map<String, String> headers) {
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
            @RequestParam("bks") List<String> barcodes,
            @RequestHeader Map<String, String> headers
    ) {
        List<TransportUnit> tus = service.findByBarcodes(barcodes.stream().map(barcodeGenerator::convert).collect(Collectors.toList()));
        return ResponseEntity.ok(augmentResults(tus, headers));
    }

    /*
     * Find all TransportUnits placed on the given Location.
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    public ResponseEntity<List<TransportUnitVO>> findTransportUnitsOn(
            @RequestParam("actualLocation") String actualLocation,
            @RequestHeader Map<String, String> headers
    ) {
        List<TransportUnit> tus = service.findOnLocation(actualLocation);
        return tus == null ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(augmentResults(tus, headers));
    }

    private List<TransportUnitVO> augmentResults(List<TransportUnit> tus, Map<String, String> headers) {
        List<TransportUnitVO> result = mapper.map(tus, TransportUnitVO.class);
        result.forEach(tu -> addLinks(tu, headers));
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
                service.findByBarcode(barcodeGenerator.convert(transportUnitBK));
                throw new ResourceExistsException(translator.translate("COMMON.TU_EXISTS", transportUnitBK), "COMMON.TU_EXISTS", transportUnitBK);
            } catch (NotFoundException nfe) {
                // thats fine we just cast the exception thrown by the service
            }
        }
        TransportUnit created = service.create(barcodeGenerator.convert(transportUnitBK), tu.getTransportUnitType(), tu.getActualLocation().getLocationId(), strict);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, created.getPersistentKey())).build();
    }

    @PostMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation", "tut"})
    public ResponseEntity<Void> createTU(
            @RequestParam(value = "bk", required = false) String transportUnitBK,
            @RequestParam("actualLocation") String actualLocation,
            @RequestParam("tut") String tut,
            @RequestParam(value = "strict", required = false) Boolean strict,
            HttpServletRequest req
    ) {
        if (Boolean.TRUE.equals(strict)) {

            if (transportUnitBK == null || transportUnitBK.isEmpty()) {
                throw new IllegalArgumentException(translator.translate("COMMON.BARCODE_MISSING"));
            }

            // check if already exists ...
            try {
                service.findByBarcode(barcodeGenerator.convert(transportUnitBK));
                throw new ResourceExistsException(translator.translate("COMMON.TU_EXISTS", transportUnitBK), "COMMON.TU_EXISTS", transportUnitBK);
            } catch (NotFoundException nfe) {
                // thats fine we just cast the exception thrown by the service
            }
        }
        TransportUnit created = transportUnitBK == null
                ? service.createNew(tut, actualLocation)
                : service.create(barcodeGenerator.convert(transportUnitBK), tut, actualLocation, strict);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, created.getPersistentKey())).build();
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
                        linkTo(methodOn(TransportUnitController.class).findTransportUnit("00000000000000004711", Collections.emptyMap())).withRel("transport-unit-findbybarcode"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnits(asList("00000000000000004711", "00000000000000004712"), Collections.emptyMap())).withRel("transport-unit-findbybarcodes"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitsOn("EXT_/0000/0000/0000/0000", Collections.emptyMap())).withRel("transport-unit-findonlocation")
                )
        );
    }
}
