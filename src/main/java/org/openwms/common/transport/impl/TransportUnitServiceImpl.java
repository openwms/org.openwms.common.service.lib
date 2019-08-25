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
package org.openwms.common.transport.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.events.TransportUnitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * A TransportUnitServiceImpl is a Spring managed bean that deals with TransportUnits.
 *
 * @author Heiko Scherrer
 */
@TxService
class TransportUnitServiceImpl implements TransportUnitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitServiceImpl.class);
    public static final String NO_LOCATION_SET = "The actualLocation must be given in order to create a TransportUnit";
    public static final String NO_BARCODE = "The barcode must be given in order to create a TransportUnit";
    public static final String NO_TRANSPORT_UNIT_TYPE = "The transportUnitType must be given in order to create a TransportUnit";

    private final TransportUnitRepository repository;
    private final LocationService locationService;
    private final TransportUnitTypeRepository transportUnitTypeRepository;
    private final Translator translator;
    private final ApplicationContext ctx;

    TransportUnitServiceImpl(Translator translator,
            TransportUnitTypeRepository transportUnitTypeRepository,
            LocationService locationService, TransportUnitRepository repository,
            ApplicationContext ctx) {
        this.translator = translator;
        this.transportUnitTypeRepository = transportUnitTypeRepository;
        this.locationService = locationService;
        this.repository = repository;
        this.ctx = ctx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit create(@NotNull Barcode barcode, @NotNull TransportUnitType transportUnitType,
            @NotNull LocationPK actualLocation, Boolean strict) {
        Assert.notNull(barcode, NO_BARCODE);
        Assert.notNull(transportUnitType, NO_TRANSPORT_UNIT_TYPE);
        Assert.notNull(actualLocation, NO_LOCATION_SET);
        return createInternal(
                barcode,
                transportUnitType.getType(),
                strict,
                () -> locationService.findByLocationId(actualLocation)
                        .orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", actualLocation)))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit create(@NotNull Barcode barcode, @NotEmpty String transportUnitType,
            @NotEmpty String actualLocation, Boolean strict) {
        Assert.notNull(actualLocation, NO_LOCATION_SET);

        return createInternal(
                barcode,
                transportUnitType,
                strict,
                () -> locationService.findByLocationId(actualLocation)
                        .orElseThrow(() -> new NotFoundException(format("No Location with actual location [%s] found", actualLocation)))
        );
    }

    private TransportUnit createInternal(Barcode barcode, String transportUnitType, Boolean strict, Supplier<Location> locationResolver) {
        Assert.notNull(barcode, NO_BARCODE);
        Assert.hasText(transportUnitType, NO_TRANSPORT_UNIT_TYPE);

        Optional<TransportUnit> opt = repository.findByBarcode(barcode);
        if (strict == null || Boolean.FALSE.equals(strict)) {
            if (opt.isPresent()) {
                LOGGER.debug("TransportUnit with Barcode [{}] already exists, silently returning the existing one", barcode);
                return opt.get();
            }
        } else {
            opt.ifPresent(tu -> {
                throw new ServiceLayerException(format("TransportUnit with id [%s] already exists", barcode));
            });
        }

        Location actualLocation = locationResolver.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating a TransportUnit with Barcode [{}] of Type [{}] on Location [{}]", barcode, transportUnitType, actualLocation);
        }
        TransportUnitType type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(() -> new ServiceLayerException(format("TransportUnitType [%s] not found", transportUnitType)));
        TransportUnit transportUnit = new TransportUnit(barcode, type, actualLocation);
        transportUnit = repository.save(transportUnit);
        ctx.publishEvent(TransportUnitEvent.newBuilder().tu(transportUnit).type(TransportUnitEvent.TransportUnitEventType.CREATED).build());
        return transportUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit update(@NotNull Barcode barcode, @NotNull TransportUnit tu) {
        if (!barcode.equals(tu.getBarcode())) {
            throw new ServiceLayerException("Mismatch between Barcode and tu.Barcode in API");
        }
        if (!repository.findByBarcode(barcode).isPresent()) {
            throw new NotFoundException(format("TransportUnit with Barcode [%s] not found", barcode));
        }
        TransportUnit saved = repository.save(tu);
        ctx.publishEvent(TransportUnitEvent.newBuilder()
                .tu(saved)
                .type(TransportUnitEvent.TransportUnitEventType.CHANGED).build()
        );
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit moveTransportUnit(Barcode barcode, LocationPK targetLocationPK) {
        TransportUnit transportUnit = repository.findByBarcode(barcode).orElseThrow(() -> new NotFoundException(format("TransportUnit with barcode [%s] not found", barcode)));
        transportUnit.setActualLocation(locationService.findByLocationId(targetLocationPK).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", targetLocationPK))));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Moving TransportUnit with barcode [{}] to Location [{}]", barcode, targetLocationPK);
        }
        TransportUnit saved = repository.save(transportUnit);
        ctx.publishEvent(TransportUnitEvent.newBuilder().tu(saved).type(TransportUnitEvent.TransportUnitEventType.MOVED).actualLocation(transportUnit.getActualLocation()).build());
        return saved;
    }

    /**
     * {@inheritDoc}
     *
     * All or nothing. Either all TransportUnits are allowed to be deleted or none is.
     */
    @Override
    @Measured
    public void deleteTransportUnits(List<TransportUnit> transportUnits) {
        if (transportUnits != null && !transportUnits.isEmpty()) {
            transportUnits.sort((o1, o2) -> o1.getChildren().isEmpty() ? -1 : 1);
            transportUnits.forEach(this::delete);
        }
    }

    @EventListener
    public void onEvent(TUCommand command) {
        if (command.getType() == TUCommand.Type.REMOVE) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Got command to REMOVE TransportUnit with id [{}]", command.getTransportUnit().getpKey());
            }
            repository.findByPKey(command.getTransportUnit().getpKey()).ifPresent(tu -> {
                repository.delete(tu);
                ctx.publishEvent(TransportUnitEvent.newBuilder().tu(tu).type(TransportUnitEvent.TransportUnitEventType.DELETED).build());
            });
        }
    }

    private void delete(TransportUnit transportUnit) {
        TransportUnitMO mo = TransportUnitMO.newBuilder()
                .withPKey(transportUnit.getPersistentKey())
                .withBarcode(transportUnit.getBarcode().getValue())
                .build();
        ctx.publishEvent(TUCommand.newBuilder().withTransportUnit(mo).withType(TUCommand.Type.REMOVING).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public TransportUnit findByBarcode(Barcode barcode) {
        return repository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.BARCODE_NOT_FOUND, new Serializable[]{barcode}, barcode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<TransportUnit> findByBarcodes(List<Barcode> barcodes) {
        List<TransportUnit> tus = repository.findByBarcodeIn(barcodes);
        return tus == null ? Collections.emptyList() : tus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<TransportUnit> findOnLocation(@NotEmpty String actualLocation) {
        Assert.hasText(actualLocation, NO_LOCATION_SET);
        Location optionalLocation = locationService.findByLocationId(actualLocation).orElseThrow(() -> new NotFoundException(format("Location [%s] not found", actualLocation)));
        return repository.findByActualLocationOrderByActualLocationDate(optionalLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit findByPKey(String pKey) {
        return repository.findByPKey(pKey).orElseThrow(() -> new NotFoundException(format("No TransportUnit with pKey [%s] found", pKey)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit changeTarget(@NotNull Barcode barcode, @NotEmpty String targetLocation) {
        TransportUnit transportUnit = repository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(format("No TransportUnit with barcode [%s] found", barcode)));

        Location location = locationService.findByLocationId(targetLocation)
                .orElseThrow(() -> new NotFoundException(format("Location with locationId [%s] not found", targetLocation)));

        transportUnit.setTargetLocation(location);
        return repository.save(transportUnit);
    }
}
