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
package org.openwms.common.location.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.location.Message;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.events.LocationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationServiceImpl is a Spring managed transactional Service that operates on {@link Location} entities and spans the tx boundary.
 *
 * @author Heiko Scherrer
 */
@TxService
class LocationServiceImpl implements LocationService {

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
    public Location removeMessages(String pKey, List<Message> messages) {
        Location location = repository
                .findByPKey(pKey)
                .orElseThrow(() -> new ServiceLayerException(format("Location with pKey [%s] not found", pKey)));
        location.removeMessages(messages.toArray(new Message[0]));
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<Location> findByLocationId(LocationPK locationPK) {
        return repository.findByLocationId(locationPK);
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
    public Optional<Location> findByLocationId(String locationPK) {
        if (!LocationPK.isValid(locationPK)) {
            throw new IllegalArgumentException(format("The given locationPK [%s] is not of valid format", locationPK));
        }
        return repository.findByLocationId(LocationPK.fromString(locationPK));
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
                .findByPKey(pKey)
                .orElseThrow(() -> new NotFoundException(format("No Location with persistent key [%s] found", pKey)));

        boolean changed = false;
        if (Optional.ofNullable(errorCode.getPlcState()).isPresent() && errorCode.getPlcState() != location.getPlcState()) {
            location.setPlcState(errorCode.getPlcState());
            changed = true;
        }
        Optional<Boolean> infeedAvailable = stateInTransformer.available(errorCode.getErrorCode());
        if (infeedAvailable.isPresent() &&
                //location.getLocationGroup().isInfeedAllowed() &&
                location.isInfeedActive() != infeedAvailable.get()) {
            location.setInfeed(infeedAvailable.get());
            changed = true;
        }
        Optional<Boolean> outfeedAvailable = stateOutTransformer.available(errorCode.getErrorCode());
        if (outfeedAvailable.isPresent() &&
                //location.getLocationGroup().isOutfeedAllowed() &&
                location.isOutfeedActive() != outfeedAvailable.get()) {
            location.setOutfeed(outfeedAvailable.get());
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
    public List<Location> findLocations(LocationPK locationPK) {
        List<Location> result = repository.findByLocationIdContaining(locationPK);
        return result == null ? Collections.emptyList() : result;
    }
}
