/*
 * Copyright 2005-2023 the original author or authors.
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
import org.ameba.exception.ResourceExistsException;
import org.ameba.exception.ServiceLayerException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitMapper;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitState;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.UnitError;
import org.openwms.common.transport.api.ValidationGroups;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.common.transport.events.TransportUnitEvent;
import org.openwms.core.exception.IllegalConfigurationValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.ameba.system.ValidationUtil.validate;
import static org.openwms.common.transport.api.commands.TUCommand.Type.REMOVING;

/**
 * A TransportUnitServiceImpl is a Spring managed bean that deals with TransportUnits.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class TransportUnitServiceImpl implements TransportUnitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitServiceImpl.class);
    private static final String NO_LOCATION_SET = "The actualLocation must be given in order to create a TransportUnit";
    private static final String NO_BARCODE = "The barcode must be given in order to create a TransportUnit";
    private static final String NO_TRANSPORT_UNIT_TYPE = "The transportUnitType must be given in order to create a TransportUnit";

    private final TransportUnitRepository repository;
    private final BarcodeGenerator barcodeGenerator;
    private final LocationService locationService;
    private final TransportUnitTypeRepository transportUnitTypeRepository;
    private final Translator translator;
    private final TransportUnitMapper mapper;
    private final Validator validator;
    private final ApplicationEventPublisher publisher;
    private final String deleteTransportUnitMode;

    @SuppressWarnings("squid:S107")
    TransportUnitServiceImpl(Translator translator,
            TransportUnitTypeRepository transportUnitTypeRepository,
            LocationService locationService, TransportUnitRepository repository,
            BarcodeGenerator barcodeGenerator, TransportUnitMapper mapper, Validator validator,
            ApplicationEventPublisher publisher, @Value("${owms.common.delete-transport-unit-mode}") String deleteTransportUnitMode) {
        this.translator = translator;
        this.transportUnitTypeRepository = transportUnitTypeRepository;
        this.locationService = locationService;
        this.repository = repository;
        this.barcodeGenerator = barcodeGenerator;
        this.mapper = mapper;
        this.validator = validator;
        this.publisher = publisher;
        this.deleteTransportUnitMode = deleteTransportUnitMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull TransportUnit create(
            @NotBlank String transportUnitBK, @NotNull TransportUnitType transportUnitType,
            @NotNull LocationPK actualLocation, Boolean strict) {
        Assert.notNull(transportUnitBK, NO_BARCODE);
        Assert.notNull(transportUnitType, NO_TRANSPORT_UNIT_TYPE);
        Assert.notNull(actualLocation, NO_LOCATION_SET);
        return createInternal(
                barcodeGenerator.convert(transportUnitBK),
                transportUnitType.getType(),
                strict,
                () -> locationService.findByLocationPk(actualLocation)
                        .orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", actualLocation)))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull TransportUnit create(
            @NotBlank String transportUnitBK, @NotBlank String transportUnitType,
            @NotBlank String actualLocation, Boolean strict) {
        Assert.notNull(actualLocation, NO_LOCATION_SET);

        return createInternal(
                barcodeGenerator.convert(transportUnitBK),
                transportUnitType,
                strict,
                () -> locationService.findByLocationId(actualLocation)
                        .orElseThrow(() -> new NotFoundException(format("No Location with actual location [%s] found", actualLocation)))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull TransportUnit createNew(@NotBlank String transportUnitType, @NotBlank String actualLocation) {
        var nextBarcode = barcodeGenerator.generate(transportUnitType, actualLocation);
        return createInternal(
                nextBarcode,
                transportUnitType,
                false,
                () -> locationService.findByLocationId(actualLocation)
                        .orElseThrow(() -> new NotFoundException(format("No Location with actual location [%s] found", actualLocation)))
        );
    }

    private TransportUnit createInternal(Barcode barcode, String transportUnitType, Boolean strict, Supplier<Location> locationResolver) {
        Assert.notNull(barcode, NO_BARCODE);
        Assert.hasText(transportUnitType, NO_TRANSPORT_UNIT_TYPE);

        var optTransportUnit = repository.findByBarcode(barcode);
        if (strict == null || Boolean.FALSE.equals(strict)) {
            if (optTransportUnit.isPresent()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("TransportUnit with Barcode [{}] already exists, silently returning the existing one", barcode);
                }
                return optTransportUnit.get();
            }
        } else {
            optTransportUnit.ifPresent(tu -> {
                throw new ResourceExistsException(format("TransportUnit with id [%s] already exists", barcode));
            });
        }

        var actualLocation = locationResolver.get();
        var type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(() -> new NotFoundException(format("TransportUnitType [%s] not found", transportUnitType)));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating a TransportUnit with Barcode [{}] of Type [{}] on Location [{}]", barcode, transportUnitType, actualLocation);
        }
        var transportUnit = repository.save(new TransportUnit(barcode, type, actualLocation));
        publisher.publishEvent(
                TransportUnitEvent.newBuilder()
                        .tu(transportUnit)
                        .type(TransportUnitEvent.TransportUnitEventType.CREATED)
                        .build()
        );
        return transportUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Validated(ValidationGroups.TransportUnit.Update.class)
    @Measured
    public @NotNull TransportUnit update(@NotNull Barcode barcode, final @Valid @NotNull TransportUnit tu) {
        if (!barcode.equals(tu.getBarcode())) {
            throw new ServiceLayerException("Mismatch between Barcode and tu.Barcode in API");
        }
        var existing = findByBarcodeInternal(barcode);
        var updated = new TransportUnit(barcode);
        updated.setTransportUnitType(existing.getTransportUnitType());
        mapper.copy(existing, updated);
        if (tu.getActualLocation() !=  null && tu.getActualLocation().isNew()) {
            existing.setActualLocation(this.locationService.findByLocationPk(tu.getActualLocation().getLocationId()).orElseThrow(() -> new NotFoundException(format("Location [%s] not found", tu.getActualLocation()))));
        }
        var saved = repository.save(existing);
        publisher.publishEvent(TransportUnitEvent.newBuilder()
                .tu(saved)
                .type(TransportUnitEvent.TransportUnitEventType.CHANGED)
                .build()
        );
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("javasecurity:S5145")
    @Override
    @Measured
    public @NotNull TransportUnit moveTransportUnit(@NotNull Barcode barcode, @NotBlank String targetLocation) {
        var transportUnit = findByBarcodeInternal(barcode);
        var previousLocation = transportUnit.getActualLocation();

        var target = LocationPK.isValid(targetLocation)
                ? locationService.findByLocationPk(LocationPK.fromString(targetLocation))
                .orElseThrow(() -> new NotFoundException(format("No Location with LocationPk [%s] found", LocationPK.fromString(targetLocation))))
                : locationService.findByErpCode(targetLocation).orElseGet(() -> locationService.findByPlcCode(targetLocation)
                .orElseThrow(() -> new NotFoundException(format("No Location with LocationPk [%s] found", LocationPK.fromString(targetLocation)))));

        if (previousLocation.getLocationId().equals(target.getLocationId())) {
            LOGGER.debug("TransportUnit [{}] is already booked on Location [{}]", barcode, target.getLocationId());
            return transportUnit;
        }
        transportUnit.setActualLocation(target);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Moving TransportUnit with barcode [{}] from Location [{}] to Location [{}]", barcode, previousLocation,
                    target.getLocationId());
        }
        var saved = repository.save(transportUnit);
        publisher.publishEvent(
                TransportUnitEvent.newBuilder()
                        .tu(saved)
                        .type(TransportUnitEvent.TransportUnitEventType.MOVED)
                        .previousLocation(previousLocation)
                        .actualLocation(transportUnit.getActualLocation())
                        .build()
        );
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void deleteTransportUnits(@NotNull List<TransportUnit> transportUnits) {
        if (!transportUnits.isEmpty()) {
            var tus = new ArrayList<>(transportUnits);
            tus.sort((o1, o2) -> {
                if (o1.hasChildren() && o2.hasChildren() ||
                        !o1.hasChildren() && !o2.hasChildren()) {
                    return 0;
                } else if (!o1.hasChildren() && o2.hasChildren()) {
                    return -1;
                } else {
                    return 1;
                }
            });
            tus.forEach(this::delete);
        }
    }

    /* we expect that the calling service spans the TX here, because the EL advice may come differently in the chain. */
    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onEvent(TUCommand command) {
        if (command.getType() == TUCommand.Type.REMOVE) {
            validate(validator, command, ValidationGroups.TransportUnit.Remove.class);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Got command to REMOVE TransportUnit with pKey [{}]", command.getTransportUnit().getpKey());
            }
            deleteDefinitely(command.getTransportUnit().getpKey());
        }
    }

    private void deleteDefinitely(String pKey) {
        repository.findByPKey(pKey).ifPresent(tu -> {
            repository.delete(tu);
            publisher.publishEvent(
                    TransportUnitEvent.newBuilder()
                            .tu(tu)
                            .type(TransportUnitEvent.TransportUnitEventType.DELETED)
                            .build());
        });
    }

    private void delete(TransportUnit transportUnit) {
        if ("strict".equalsIgnoreCase(deleteTransportUnitMode)) {
            deleteDefinitely(transportUnit.getPersistentKey());
        } else if ("on-accept".equalsIgnoreCase(deleteTransportUnitMode)) {
            var mo = TransportUnitMO.newBuilder()
                    .withPKey(transportUnit.getPersistentKey())
                    .withBarcode(transportUnit.getBarcode().getValue())
                    .build();
            publisher.publishEvent(TUCommand.newBuilder(REMOVING)
                    .withTransportUnit(mo)
                    .build()
            );
        } else {
            throw new IllegalConfigurationValueException(format("Configuration value [owms.common.delete-transport-unit-mode] is configured with invalid value [%s]", deleteTransportUnitMode));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull TransportUnit findByBarcode(@NotBlank String transportUnitBK) {
        return findByBarcodeInternal(barcodeGenerator.convert(transportUnitBK));
    }

    private TransportUnit findByBarcodeInternal(Barcode barcode) {
        return repository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.TU_BARCODE_NOT_FOUND, new Serializable[]{barcode}, barcode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<TransportUnit> findByBarcodes(@NotEmpty List<Barcode> barcodes) {
        var tus = repository.findByBarcodeIn(barcodes);
        return tus == null ? new ArrayList<>(0) : tus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<TransportUnit> findOnLocation(@NotBlank String actualLocation) {
        Assert.hasText(actualLocation, NO_LOCATION_SET);
        var location = locationService.findByLocationIdOrThrow(actualLocation);
        return repository.findByActualLocationOrderByActualLocationDate(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull TransportUnit findByPKey(@NotBlank String pKey) {
        return repository.findByPKey(pKey).orElseThrow(() -> new NotFoundException(format("No TransportUnit with pKey [%s] found", pKey)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void addError(@NotBlank String transportUnitBK, @NotNull UnitError unitError) {
        var tu = this.findByBarcodeInternal(barcodeGenerator.convert(transportUnitBK));
        tu.addError(unitError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<TransportUnit> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnit changeTarget(@NotNull Barcode barcode, @NotBlank String targetLocation) {
        var transportUnit = findByBarcodeInternal(barcode);
        var location = locationService.findByLocationIdOrThrow(targetLocation);
        transportUnit.setTargetLocation(location);
        var saved = repository.save(transportUnit);
        publisher.publishEvent(TransportUnitEvent.newBuilder()
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
    public void setState(@NotBlank String transportUnitBK, @NotNull TransportUnitState state) {
        var transportUnit = findByBarcodeInternal(barcodeGenerator.convert(transportUnitBK));
        LOGGER.debug("Setting TransportUnit [{}] to state [{}]", transportUnitBK, state);
        transportUnit.setState(state);
        publisher.publishEvent(TransportUnitEvent.newBuilder()
                .tu(transportUnit)
                .type(TransportUnitEvent.TransportUnitEventType.STATE_CHANGE).build()
        );
        repository.save(transportUnit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void synchronizeTransportUnits() {
        var all = repository.findAll();
        all.forEach(tu -> publisher.publishEvent(
                TransportUnitEvent.newBuilder()
                        .tu(tu)
                        .type(TransportUnitEvent.TransportUnitEventType.CREATED)
                        .build()
        ));
    }
}
