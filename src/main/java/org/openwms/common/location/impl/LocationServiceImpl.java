/*
 * Copyright 2005-2021 the original author or authors.
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
package org.openwms.common.location.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.i18n.Translator;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.events.LocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.openwms.common.CommonMessageCodes.LOCATION_ID_EXISTS;
import static org.openwms.common.CommonMessageCodes.LOCATION_ID_INVALID;
import static org.openwms.common.CommonMessageCodes.LOCATION_NOT_FOUND;

/**
 * A LocationServiceImpl is a Spring managed transactional Service that operates on {@link Location} entities and spans the tx boundary.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationServiceImpl.class);
    private final Translator translator;
    private final LocationRepository repository;
    private final ErrorCodeTransformers.LocationStateIn stateInTransformer;
    private final ErrorCodeTransformers.LocationStateOut stateOutTransformer;
    private final ApplicationContext ctx;

    LocationServiceImpl(Translator translator, LocationRepository repository, ErrorCodeTransformers.LocationStateIn stateInTransformer,
                        ErrorCodeTransformers.LocationStateOut stateOutTransformer, ApplicationContext ctx) {
        this.translator = translator;
        this.repository = repository;
        this.stateInTransformer = stateInTransformer;
        this.stateOutTransformer = stateOutTransformer;
        this.ctx = ctx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Location create(@NotNull Location location) {
        Optional<Location> locationOpt = repository.findByLocationId(location.getLocationId());
        if (location.hasLocationId() && locationOpt.isPresent()) {
            throw new ResourceExistsException(translator, LOCATION_ID_EXISTS,
                    new Serializable[]{location.getLocationId()},
                    location.getLocationId());
        };
        return repository.save(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<Location> findByLocationPk(@NotNull LocationPK locationId) {
        return repository.findByLocationId(locationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<Location> findByPlcCode(@NotEmpty String plcCode) {
        return repository.findByPlcCode(plcCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<Location> findByLocationId(@NotEmpty String locationId) {
        if (!LocationPK.isValid(locationId)) {
            throw new IllegalArgumentException(translator.translate(LOCATION_ID_INVALID, locationId));
        }
        return repository.findByLocationId(LocationPK.fromString(locationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<Location> findAllOf(@NotEmpty List<String> locationGroupNames) {
        return locationGroupNames.size() == 1
                ? repository.findByLocationGroup_Name(locationGroupNames.get(0))
                : repository.findByLocationGroup_Name(locationGroupNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeState(@NotEmpty String pKey, @NotNull ErrorCodeVO errorCode) {
        Location location = repository
                .findBypKey(pKey)
                .orElseThrow(() -> new NotFoundException(translator, LOCATION_NOT_FOUND, new String[]{pKey}, pKey));

        boolean changed = false;
        if (Optional.ofNullable(errorCode.getPlcState()).isPresent() && errorCode.getPlcState() != location.getPlcState()) {
            location.setPlcState(errorCode.getPlcState());
            LOGGER.info("PLC state of location [{}] has been updated to [{}]", location.getLocationId(), errorCode.getPlcState());
            changed = true;
        }
        Optional<Boolean> infeedAvailable = stateInTransformer.available(errorCode.getErrorCode());
        if (infeedAvailable.isPresent() &&
                //location.getLocationGroup().isInfeedAllowed() &&
                !infeedAvailable.get().equals(location.isInfeedActive())) {
            location.setInfeed(infeedAvailable.get());
            LOGGER.info("Incoming active of location [{}] has been updated to [{}]", location.getLocationId(), infeedAvailable.get());
            changed = true;
        }
        Optional<Boolean> outfeedAvailable = stateOutTransformer.available(errorCode.getErrorCode());
        if (outfeedAvailable.isPresent() &&
                //location.getLocationGroup().isOutfeedAllowed() &&
                !outfeedAvailable.get().equals(location.isOutfeedActive())) {
            location.setOutfeed(outfeedAvailable.get());
            LOGGER.info("Outgoing active of location [{}] has been updated to [{}]", location.getLocationId(), outfeedAvailable.get());
            changed = true;
        }
        if (changed) {
            // don't send twice only if one has changed
            ctx.publishEvent(LocationEvent.of(location, LocationEvent.LocationEventType.STATE_CHANGE));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<Location> findLocations(@NotNull LocationPK locationPK) {
        List<Location> result = repository.findByLocationIdContaining(locationPK);
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<Location> findByErpCode(@NotEmpty String erpCode) {
        return repository.findByErpCode(erpCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Location save(@NotNull Location location) {
        if (location.isNew()) {
            throw new IllegalArgumentException("Expected to save an already existing instance but got a transient one");
        }
        return repository.save(location);
    }
}
