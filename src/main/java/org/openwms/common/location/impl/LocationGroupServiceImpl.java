/*
 * Copyright 2005-2022 the original author or authors.
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
import org.ameba.i18n.Translator;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupService;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.events.LocationGroupEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.openwms.common.CommonMessageCodes.LOCATION_GROUP_NOT_FOUND;
import static org.openwms.common.CommonMessageCodes.LOCATION_GROUP_NOT_FOUND_BY_PKEY;

/**
 * A LocationGroupServiceImpl is a Spring managed transactional Service that operates on {@link LocationGroup} entities and spans the
 * tx boundary.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class LocationGroupServiceImpl implements LocationGroupService {

    private final LocationGroupRepository repository;
    private final ApplicationContext ctx;
    private final Translator translator;

    LocationGroupServiceImpl(LocationGroupRepository repository, ApplicationContext ctx, Translator translator) {
        this.repository = repository;
        this.ctx = ctx;
        this.translator = translator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupState(@NotEmpty String pKey, @NotNull LocationGroupState stateIn, @NotNull LocationGroupState stateOut) {
        LocationGroup locationGroup = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                translator, LOCATION_GROUP_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey
        ));
        locationGroup.changeState(stateIn, stateOut);
        ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.STATE_CHANGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupStates(@NotEmpty String name, Optional<LocationGroupState> stateIn, Optional<LocationGroupState> stateOut) {
        LocationGroup locationGroup = repository
                .findByName(name)
                .orElseThrow(() -> new NotFoundException(translator, LOCATION_GROUP_NOT_FOUND, new String[]{name}, name));
        stateIn.ifPresent(locationGroup::changeGroupStateIn);
        stateOut.ifPresent(locationGroup::changeGroupStateOut);
        if (stateIn.isPresent() || stateOut.isPresent()) {
            ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.STATE_CHANGE));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeOperationMode(@NotEmpty String name, @NotEmpty String mode) {
        LocationGroup locationGroup = repository
                .findByName(name)
                .orElseThrow(() -> new NotFoundException(translator, LOCATION_GROUP_NOT_FOUND, new String[]{name}, name));
        locationGroup.setOperationMode(mode);
        repository.save(locationGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationGroup> findByName(@NotEmpty String name) {
        return repository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<LocationGroup> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<LocationGroup> findByNames(@NotEmpty List<String> locationGroupNames) {
        List<LocationGroup> result = repository.findByNameIn(locationGroupNames);
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public LocationGroup save(@NotNull LocationGroup locationGroup) {
        return repository.save(locationGroup);
    }
}