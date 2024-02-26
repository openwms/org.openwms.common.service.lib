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
import org.ameba.i18n.Translator;
import org.openwms.common.location.LocationType;
import org.openwms.common.location.LocationTypeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.openwms.common.CommonMessageCodes.LOCATION_TYPE_NOT_FOUND_BY_PKEY;

/**
 * A LocationTypeServiceImpl is a Spring managed transactional Service that operates on {@link LocationType} entities and spans the
 * tx boundary.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class LocationTypeServiceImpl implements LocationTypeService {

    private final Translator translator;
    private final LocationTypeRepository repository;

    LocationTypeServiceImpl(Translator translator, LocationTypeRepository repository) {
        this.translator = translator;
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @NotNull public LocationType findByPKey(@NotBlank String pKey) {
        return findInternal(pKey);
    }

    private LocationType findInternal(String pKey) {
        return repository
                .findBypKey(pKey)
                .orElseThrow(() -> new NotFoundException(translator, LOCATION_TYPE_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationType> findByTypeName(@NotBlank String typeName) {
        return repository.findByType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public @NotNull List<LocationType> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * The implementation uses the PK to find the LocationType to be removed and removes it.
     */
    @Override
    @Measured
    public void delete(@NotNull List<LocationType> locationTypes) {
        locationTypes.forEach(
                locationType -> repository
                        .findByType(locationType.getType())
                        .ifPresent(t -> repository.deleteById(t.getPk())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull LocationType save(@NotNull LocationType locationType) {
        return repository.save(locationType);
    }
}
