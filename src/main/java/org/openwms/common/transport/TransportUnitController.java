/*
 * Copyright 2005-2024 the original author or authors.
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
import org.openwms.common.SimpleLink;
import org.openwms.common.location.LocationController;
import org.openwms.common.transport.api.TransportApiConstants;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.ValidationGroups;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.core.SpringProfiles;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static java.util.Arrays.asList;
import static org.openwms.common.CommonMessageCodes.TU_BARCODE_MISSING;
import static org.openwms.common.CommonMessageCodes.TU_EXISTS;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNITS;
import static org.openwms.common.transport.api.TransportUnitVO.MEDIA_TYPE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A TransportUnitController.
 *
 * @author Heiko Scherrer
 */
@Profile("!" + SpringProfiles.IN_MEMORY)
@Validated
@MeasuredRestController
public class TransportUnitController extends AbstractWebController {

    private final TransportUnitMapper mapper;
    private final Translator translator;
    private final BarcodeGenerator barcodeGenerator;
    private final TransportUnitService service;

    TransportUnitController(TransportUnitMapper mapper, Translator translator, BarcodeGenerator barcodeGenerator, TransportUnitService service) {
        this.mapper = mapper;
        this.translator = translator;
        this.barcodeGenerator = barcodeGenerator;
        this.service = service;
    }

    @GetMapping(value = API_TRANSPORT_UNITS + "/{pKey}", produces = MEDIA_TYPE)
    public ResponseEntity<TransportUnitVO> findTransportUnitByPKey(
            @PathVariable("pKey") String pKey
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.findByPKey(pKey))
        );
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = MEDIA_TYPE)
    public ResponseEntity<TransportUnitVO> findTransportUnit(
            @RequestParam("bk") String transportUnitBK
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.findByBarcode(transportUnitBK))
        );
    }

    @GetMapping(value = API_TRANSPORT_UNITS, produces = MEDIA_TYPE)
    public ResponseEntity<List<TransportUnitVO>> findAll() {
        return ResponseEntity.ok(
                convertAndLinks(service.findAll())
        );
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bks"}, produces = MEDIA_TYPE)
    public ResponseEntity<List<TransportUnitVO>> findTransportUnits(
            @RequestParam("bks") @NotEmpty List<String> barcodes
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.findByBarcodes(barcodes.stream().map(barcodeGenerator::convert).toList()))
        );
    }

    @GetMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation"}, produces = MEDIA_TYPE)
    public ResponseEntity<List<TransportUnitVO>> findTransportUnitsOn(
            @RequestParam("actualLocation") String actualLocation
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.findOnLocation(actualLocation))
        );
    }

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
                throw new ResourceExistsException(translator.translate(TU_EXISTS, transportUnitBK), TU_EXISTS, transportUnitBK);
            } catch (NotFoundException nfe) {
                // thats fine we just cast the exception thrown by the service
            }
        }
        var created = service.create(transportUnitBK, tu.getTransportUnitType().getType(), tu.getActualLocation().getLocationId(), strict);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, created.getPersistentKey())).build();
    }

    @PostMapping(API_TRANSPORT_UNITS + "/synchronize")
    public void synchronizeTU() {
        service.synchronizeTransportUnits();
    }

    @PostMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation", "tut"}, produces = MEDIA_TYPE)
    public ResponseEntity<TransportUnitVO> createTU(
            @RequestParam(value = "bk", required = false) String transportUnitBK,
            @RequestParam("actualLocation") String actualLocation,
            @RequestParam("tut") String tut,
            @RequestParam(value = "strict", required = false) Boolean strict,
            HttpServletRequest req
    ) {
        if (Boolean.TRUE.equals(strict)) {

            if (transportUnitBK == null || transportUnitBK.isEmpty()) {
                throw new IllegalArgumentException(translator.translate(TU_BARCODE_MISSING));
            }

            // check if already exists ...
            try {
                service.findByBarcode(transportUnitBK);
                throw new ResourceExistsException(translator.translate(TU_EXISTS, transportUnitBK), TU_EXISTS, transportUnitBK);
            } catch (NotFoundException nfe) {
                // that's fine we just cast the exception thrown by the service
            }
        }
        var created = transportUnitBK == null
                ? service.createNew(tut, actualLocation)
                : service.create(transportUnitBK, tut, actualLocation, strict);
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, created.getPersistentKey()))
                .body(convertAndLinks(created))
                ;
    }

    @Validated(ValidationGroups.TransportUnit.Update.class)
    @PutMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = MEDIA_TYPE)
    public ResponseEntity<TransportUnitVO> updateTU(
            @RequestParam("bk") String transportUnitBK,
            @Valid @RequestBody TransportUnitVO tu
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.update(barcodeGenerator.convert(transportUnitBK), mapper.convert(tu)))
        );
    }

    @PatchMapping(value = API_TRANSPORT_UNITS, params = {"bk", "newLocation"}, produces = MEDIA_TYPE)
    public ResponseEntity<TransportUnitVO> moveTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("newLocation") String newLocation
    ) {
        return ResponseEntity.ok(
                convertAndLinks(service.moveTransportUnit(barcodeGenerator.convert(transportUnitBK), newLocation))
        );
    }

    @PostMapping(value = API_TRANSPORT_UNIT + "/error", params = {"bk", "errorCode"}, produces = MEDIA_TYPE)
    public ResponseEntity<Void> addErrorToTransportUnit(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam(value = "errorCode") String errorCode
    ) {
        service.addError(transportUnitBK, UnitError.newBuilder()
                .errorNo(errorCode)
                .build()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(API_TRANSPORT_UNITS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(TransportUnitController.class).createTU("{transportUnitBK}", null, true, null)).withRel("transport-unit-createtuwithbody"),
                        linkTo(methodOn(TransportUnitController.class).createTU("{transportUnitBK}", "{actualLocation}", "{transportUnitType}", true, null)).withRel("transport-unit-createtuwithparams"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitByPKey("1")).withRel("transport-unit-findbypkey"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnit("{transportUnitBK}")).withRel("transport-unit-findbybarcode"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnits(asList("{transportUnitBK-1}", "{transportUnitBK-n}"))).withRel("transport-unit-findbybarcodes"),
                        linkTo(methodOn(TransportUnitController.class).findTransportUnitsOn("{actualLocation.locationId}")).withRel("transport-unit-findonlocation"),
                        linkTo(methodOn(TransportUnitController.class).blockTransportUnit("{transportUnitBK}")).withRel("transport-unit-block"),
                        linkTo(methodOn(TransportUnitController.class).unblockTransportUnit("{transportUnitBK}")).withRel("transport-unit-unblock"),
                        linkTo(methodOn(TransportUnitController.class).qcTransportUnit("{transportUnitBK}")).withRel("transport-unit-qc")
                )
        );
    }

    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNITS + "/block", params = {"bk"})
    public ResponseEntity<Void> blockTransportUnit(@NotBlank @RequestParam("bk") String transportUnitBK) {
        service.setState(transportUnitBK, TransportUnitState.BLOCKED);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNITS + "/available", params = {"bk"})
    public ResponseEntity<Void> unblockTransportUnit(@NotBlank @RequestParam("bk") String transportUnitBK) {
        service.setState(transportUnitBK, TransportUnitState.AVAILABLE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = TransportApiConstants.API_TRANSPORT_UNITS + "/quality-check", params = {"bk"})
    public ResponseEntity<Void> qcTransportUnit(@NotBlank @RequestParam("bk") String transportUnitBK) {
        service.setState(transportUnitBK, TransportUnitState.QUALITY_CHECK);
        return ResponseEntity.noContent().build();
    }

    private TransportUnitVO addLinks(TransportUnitVO result) {
        result.add(
                new SimpleLink(linkTo(methodOn(TransportUnitController.class).findTransportUnitByPKey(result.getpKey())).withSelfRel()),
                new SimpleLink(linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitType(result.getTransportUnitType().getType())).withRel("transport-unit-type"))
        );
        if (result.getActualLocation() != null) {
            result.add(
                    new SimpleLink(linkTo(methodOn(LocationController.class).findByCoordinate(result.getActualLocation().getLocationId())).withRel("actual-location"))
            );
        }
        return result;
    }

    private TransportUnitVO convertAndLinks(TransportUnit entity) {
        return addLinks(
                mapper.convertToVO(entity)
        );
    }

    private List<TransportUnitVO> convertAndLinks(List<TransportUnit> entities) {
        return entities.stream()
                .map(mapper::convertToVO)
                .map(this::addLinks)
                .toList();
    }
}
