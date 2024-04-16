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
package org.openwms.common.location.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.account.AccountService;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupService;
import org.openwms.common.location.LocationRemovalManager;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.openwms.common.location.api.ValidationGroups;
import org.openwms.common.location.api.events.LocationGroupEvent;
import org.openwms.common.location.events.DeletionFailedEvent;
import org.openwms.core.listener.RemovalNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openwms.common.CommonMessageCodes.LOCATION_GROUP_EXISTS;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationGroupServiceImpl.class);
    private final ApplicationContext ctx;
    private final Translator translator;
    private final LocationGroupRepository repository;
    private final AccountService accountService;
    private final LocationRemovalManager locationRemovalManager;

    LocationGroupServiceImpl(ApplicationContext ctx, Translator translator, LocationGroupRepository repository, AccountService accountService, LocationRemovalManager locationRemovalManager) {
        this.ctx = ctx;
        this.translator = translator;
        this.repository = repository;
        this.accountService = accountService;
        this.locationRemovalManager = locationRemovalManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull LocationGroup create(@NotNull @Validated(ValidationGroups.Create.class) @Valid LocationGroupVO vo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create a LocationGroup with the VO [{}]", vo.allFieldsToString());
        }
        var eo = createLocationGroup(vo);
        var savedEo = repository.save(eo);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Request to create a new LocationGroup [{}]", savedEo);
        }
        ctx.publishEvent(LocationGroupEvent.of(savedEo, LocationGroupEvent.LocationGroupEventType.CREATED));
        return savedEo;
    }

    private LocationGroup createLocationGroup(LocationGroupVO vo) {
        var eoOpt = repository.findByName(vo.getName());
        if (eoOpt.isPresent()) {
            throw new ResourceExistsException(translator, LOCATION_GROUP_EXISTS, new String[]{vo.getName()}, vo.getName());
        }
        var eo = new LocationGroup(vo.getName());
        if (vo.getAccountId() != null && !vo.getAccountId().isEmpty()) {
            var accountOpt = accountService.findByIdentifier(vo.getAccountId());
            if (accountOpt.isEmpty()) {
                throw new NotFoundException(translator, CommonMessageCodes.ACCOUNT_NOT_FOUND_BY_ID, new String[]{vo.getAccountId()}, vo.getAccountId());
            }
            eo.setAccount(accountOpt.get());
        }
        eo.setGroupType(vo.getGroupType());
        if (vo.getParent() != null && !vo.getParent().isEmpty()) {
            var parentOpt = repository.findByName(vo.getParent());
            if (parentOpt.isEmpty()) {
                throw new NotFoundException(translator, CommonMessageCodes.LOCATION_GROUP_NOT_FOUND, new String[]{vo.getParent()}, vo.getParent());
            }
            eo.setParent(parentOpt.get());
        }
        eo.setOperationMode(vo.getOperationMode());

        if (vo.getGroupStateIn() != null) {
            eo.changeGroupStateIn(vo.getGroupStateIn());
        }

        if (vo.getGroupStateOut() != null) {
            eo.changeGroupStateOut(vo.getGroupStateOut());
        }
        if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
            eo.setLocationGroups(vo.getChildren().stream().map(this::createLocationGroup).collect(Collectors.toSet()));
        }
        return eo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupState(@NotBlank String pKey, @NotNull LocationGroupState stateIn, @NotNull LocationGroupState stateOut) {
        var locationGroup = findInternalByPKey(pKey);
        locationGroup.changeState(stateIn, stateOut);
        ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.STATE_CHANGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupStates(@NotBlank String name, Optional<LocationGroupState> stateIn, Optional<LocationGroupState> stateOut) {
        var locationGroup = findByNameOrThrowInternal(name);
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
    public void changeOperationMode(@NotBlank String name, @NotBlank String mode) {
        var locationGroup = findByNameOrThrowInternal(name);
        locationGroup.setOperationMode(mode);
        repository.save(locationGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationGroup> findByName(@NotBlank String name) {
        return repository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull LocationGroup findByNameOrThrow(@NotBlank String name) {
        return findByNameOrThrowInternal(name);
    }

    private LocationGroup findByNameOrThrowInternal(String name) {
        return repository.findByName(name).orElseThrow(() -> new NotFoundException(
                translator, LOCATION_GROUP_NOT_FOUND, new String[]{name}, name
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<LocationGroup> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<LocationGroup> findByNames(@NotEmpty List<String> locationGroupNames) {
        var result = repository.findByNameIn(locationGroupNames);
        return result == null ? new ArrayList<>(0) : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void delete(@NotBlank String pKey) {
        var locationGroup = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                translator, LOCATION_GROUP_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey
        ));
        delete(locationGroup);
    }

    private Stream<String> getLocationKeys(LocationGroup locationGroup) {
        var result = locationGroup.getLocations().stream().map(Location::getPersistentKey);
        if (locationGroup.hasLocationGroups()) {
            for (var group : locationGroup.getLocationGroups()) {
                result = Stream.concat(result, getLocationKeys(group));
            }
        }
        return result;
    }

    private void delete(LocationGroup locationGroup) {
        LOGGER.info("Going to delete LocationGroup [{}]", locationGroup.getName());
        var allLocationKeys = getLocationKeys(locationGroup).toList();

        // first check all Locations of all LocationGroups if they're allowed to be deleted.
        // Check within the service if any TU is booked on the Locations and then ask foreign services if deletion is okay
        var allowedToDelete = locationRemovalManager.allowedToDelete(allLocationKeys);

        if (!allowedToDelete) {
           throw new RemovalNotAllowedException("At least one Location is not allowed to be deleted, therefore the LocationGroup [%s] cannot be deleted".formatted(locationGroup.getName()));
        }

        // Go and mark all for deletion...
        locationRemovalManager.markForDeletion(allLocationKeys);

        // Finally delete the LocationGroups...
        try {
            deleteOnlyGroups(locationGroup);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.debug("Deletion of LocationGroup [{}] went wrong, rolling back Location deletion", locationGroup.getPersistentKey());
            // if any failure occurs, send a persistent async message to release the deletion for everyone
            for (var pKey : allLocationKeys) {
                ctx.publishEvent(new DeletionFailedEvent(pKey));
            }
        }
    }

    private void deleteOnlyGroups(LocationGroup locationGroup) {
        if (locationGroup.hasLocationGroups()) {
            for (var group : locationGroup.getLocationGroups()) {
                deleteOnlyGroups(group);
            }
        }
        if (locationGroup.hasLocations()) {
            locationRemovalManager.deleteAll(locationGroup.getLocations());
        }
        repository.delete(locationGroup);
        LOGGER.debug("LocationGroup deleted [{}]", locationGroup.getPersistentKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull LocationGroup update(@NotBlank String pKey, @NotNull LocationGroupVO locationGroupVO) {
        var locationGroup = findInternalByPKey(pKey);
        if (locationGroupVO.getDescription() != null && !locationGroupVO.getDescription().equals(locationGroup.getDescription())) {
            locationGroup.setDescription(locationGroupVO.getDescription());
            locationGroup = repository.save(locationGroup);
            ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.CHANGED));
        }
        if (locationGroupVO.hasParent() && !locationGroupVO.getParent().equals(locationGroup.getParent().getName())) {
            var newParent = findByNameOrThrowInternal(locationGroupVO.getParent());
            locationGroup.setParent(newParent);
            locationGroup = repository.save(locationGroup);
            ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.CHANGED));
        }
        return locationGroup;
    }

    private LocationGroup findInternalByPKey(String pKey) {
        return repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                translator, LOCATION_GROUP_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey
        ));
    }
}