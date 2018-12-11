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

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.core.listener.OnRemovalListener;
import org.openwms.core.listener.RemovalNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A TransportUnitServiceImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class TransportUnitServiceImpl implements TransportUnitService<TransportUnit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitServiceImpl.class);

    private final TransportUnitRepository repository;
    private final LocationService locationService;
    private final TransportUnitTypeRepository transportUnitTypeRepository;
    private List<OnRemovalListener<TransportUnit>> onRemovalListeners;
    //@Autowired
    private LocaleResolver localeResolver;
    private final Translator translator;
    private final ApplicationContext ctx;

    @Autowired
    TransportUnitServiceImpl(Translator translator, TransportUnitTypeRepository transportUnitTypeRepository, LocationService locationService, TransportUnitRepository repository, @Autowired(required = false) List<OnRemovalListener<TransportUnit>> onRemovalListeners, ApplicationContext ctx) {
        this.translator = translator;
        this.transportUnitTypeRepository = transportUnitTypeRepository;
        this.locationService = locationService;
        this.repository = repository;
        this.onRemovalListeners = onRemovalListeners;
        this.ctx = ctx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnit create(Barcode barcode, TransportUnitType transportUnitType, LocationPK actualLocation, Boolean strict) {
        Assert.notNull(barcode, "The barcode must be given in order to create a TransportUnit");
        Assert.notNull(transportUnitType, "The transportUnitType must be given in order to create a TransportUnit");
        Assert.notNull(actualLocation, "The actualLocation must be given in order to create a TransportUnit");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating a TransportUnit with Barcode {} of Type {} on Location ", barcode, transportUnitType.getType(), actualLocation);
        }
        TransportUnit transportUnit;
        if (Boolean.TRUE == strict) {
            repository.findByBarcode(barcode).ifPresent(tu -> {
                throw new ServiceLayerException(format("TransportUnit with id %s not found", barcode));
            });
        }
        Location location = locationService.findByLocationId(actualLocation).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", actualLocation)));
        TransportUnitType type = transportUnitTypeRepository.findByType(transportUnitType.getType()).orElseThrow(() -> new ServiceLayerException(format("TransportUnitType %s not found", transportUnitType)));
        transportUnit = new TransportUnit(barcode);
        transportUnit.setTransportUnitType(type);
        transportUnit.setActualLocation(location);
        transportUnit = repository.save(transportUnit);
        ctx.publishEvent(TransportUnitEvent.of(transportUnit, TransportUnitEvent.TransportUnitEventType.CREATED));
        return transportUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnit create(Barcode barcode, String transportUnitType, String actualLocation, Boolean strict) {
        Assert.notNull(barcode, "The barcode must be given in order to create a TransportUnit");
        Assert.notNull(transportUnitType, "The transportUnitType must be given in order to create a TransportUnit");
        Assert.notNull(actualLocation, "The actualLocation must be given in order to create a TransportUnit");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating a TransportUnit with Barcode {} of Type {} on Location ", barcode, transportUnitType, actualLocation);
        }
        Optional<TransportUnit> opt = repository.findByBarcode(barcode);
        if (Boolean.TRUE == strict) {
            opt.ifPresent(tu -> {
                throw new ServiceLayerException(format("TransportUnit with id %s not found", barcode));
            });
        } else {
            if (opt.isPresent()) {
                LOGGER.debug("TransportUnit with Barcode {} already exists, silently returning the existing one and continue", barcode);
                return opt.get();
            }
        }

        Location location = locationService.findByLocationIdOrPlcCode(actualLocation).orElseThrow(() -> new NotFoundException(format("No Location with actual location [%s] found", actualLocation), null));
        TransportUnitType type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(() -> new ServiceLayerException(format("TransportUnitType %s not found", transportUnitType)));
        TransportUnit transportUnit = new TransportUnit(barcode);
        transportUnit.setTransportUnitType(type);
        transportUnit.setActualLocation(location);
        transportUnit = repository.save(transportUnit);
        ctx.publishEvent(TransportUnitEvent.of(transportUnit, TransportUnitEvent.TransportUnitEventType.CREATED));
        return transportUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnit update(Barcode barcode, TransportUnit tu) {
        TransportUnit saved = repository.save(tu);
        ctx.publishEvent(TransportUnitEvent.of(saved, TransportUnitEvent.TransportUnitEventType.CHANGED));
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransportUnit> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public TransportUnit moveTransportUnit(Barcode barcode, LocationPK targetLocationPK) {
        TransportUnit transportUnit = repository.findByBarcode(barcode).orElseThrow(() -> new ServiceLayerException("TransportUnit with id " + barcode + " not found"));
        transportUnit.setActualLocation(locationService.findByLocationId(targetLocationPK).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", targetLocationPK))));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(format("Moving TransportUnit with barcode [%s] to Location [%s]", barcode, targetLocationPK));
        }
        TransportUnit saved = repository.save(transportUnit);
        ctx.publishEvent(TransportUnitEvent.of(saved, TransportUnitEvent.TransportUnitEventType.MOVED));
        return saved;
    }

    /**
     * {@inheritDoc}
     *
     * All or nothing. Either all TransportUnits are allowed to be deleted or none is.
     */
    @Override
    public void deleteTransportUnits(List<TransportUnit> transportUnits) {
        if (transportUnits != null && !transportUnits.isEmpty()) {
            transportUnits.sort((o1, o2) -> o1.getChildren().isEmpty() ? -1 : 1);
            for (TransportUnit tu : transportUnits) {
                boolean delete = delete(tu);
                if (delete) {
                    LOGGER.debug("Successfully marked TransportUnit for removal : [{}]", tu.getPk());
                } else {
                    throw new ServiceLayerException(format("Not allowed to remove TransportUnit with id [%s]", tu.getPk()));
                }
            }
        }
    }

    private boolean delete(TransportUnit transportUnit) throws RemovalNotAllowedException {
        if (null != onRemovalListeners) {
            return onRemovalListeners.stream().allMatch(l -> l.preRemove(transportUnit));
        }
        repository.delete(transportUnit);
        ctx.publishEvent(TransportUnitEvent.of(transportUnit, TransportUnitEvent.TransportUnitEventType.DELETED));
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TransportUnit findByBarcode(Barcode barcode) {
        return repository.findByBarcode(barcode).orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.BARCODE_NOT_FOUND, new Serializable[]{barcode}, barcode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportUnit> findOnLocation(String actualLocation) {
        List<TransportUnit> tus = repository.findByActualLocationOrderByActualLocationDate(locationService.findByLocationId(actualLocation));
        return tus;
    }
}