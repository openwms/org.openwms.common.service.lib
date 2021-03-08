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
package org.openwms.common.location.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationServiceImpl is a Spring managed transactional Service that operates on {@link Location} entities and spans the tx boundary.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationServiceImpl.class);
    private final LocationRepository repository;
    private final ErrorCodeTransformers.LocationStateIn stateInTransformer;
    private final ErrorCodeTransformers.LocationStateOut stateOutTransformer;
    private final ApplicationContext ctx;

    LocationServiceImpl(LocationRepository repository, ErrorCodeTransformers.LocationStateIn stateInTransformer,
            ErrorCodeTransformers.LocationStateOut stateOutTransformer, ApplicationContext ctx) {
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
    public Optional<Location> findByPlcCode(String plcCode) {
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
            throw new IllegalArgumentException(format("The given locationPK [%s] is not of valid format", locationId));
        }
        return repository.findByLocationId(LocationPK.fromString(locationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<Location> findAllOf(List<String> locationGroupNames) {
        return locationGroupNames.size() == 1
                ? repository.findByLocationGroup_Name(locationGroupNames.get(0))
                : repository.findByLocationGroup_Name(locationGroupNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeState(String pKey, ErrorCodeVO errorCode) {
        Location location = repository
                .findBypKey(pKey)
                .orElseThrow(() -> new NotFoundException(format("No Location with persistent key [%s] found", pKey)));

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
    public Location save(@NotNull Location location) {
        if (location.isNew()) {
            throw new NotFoundException("Expected to save an already existing instance but got a transient one");

        }
        return repository.save(location);
    }
}
