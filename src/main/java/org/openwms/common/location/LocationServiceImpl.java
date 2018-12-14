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
package org.openwms.common.location;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationServiceImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;

    LocationServiceImpl(LocationRepository locationRepository, LocationTypeRepository locationTypeRepository) {
        this.locationRepository = locationRepository;
        this.locationTypeRepository = locationTypeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Location removeMessages(Long id, List<Message> messages) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new ServiceLayerException(format("Location with pk [%s] not found, probably it was removed before", id)));
        location.removeMessages(messages.toArray(new Message[0]));
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<LocationType> getAllLocationTypes() {
        return locationTypeRepository.findAll();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation uses the id to find the {@link LocationType} to be removed and will removed the type when found.
     */
    @Override
    @Measured
    public void deleteLocationTypes(List<LocationType> locationTypes) {
        locationTypes.forEach(locationType -> locationTypeRepository.deleteById(locationType.getPk()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public LocationType saveLocationType(LocationType locationType) {
        return locationTypeRepository.save(locationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<Location> findByLocationId(LocationPK locationPK) {
        return locationRepository.findByLocationId(locationPK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<Location> findByPlcCode(String plcCode) {
        return locationRepository.findByPlcCode(plcCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Location findByLocationId(String locationPK) {
        return locationRepository.findByLocationId(LocationPK.fromString(locationPK)).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", locationPK), null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Location> findByLocationIdOrPlcCode(String location) {
        if (LocationPK.isValid(location)) {
            return locationRepository.findByLocationId(LocationPK.fromString(location));
        } else {
            return locationRepository.findByPlcCode(location);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<Location> findAllOf(List<String> locationGroupNames) {
        return locationRepository.findByLocationGroup_Name(locationGroupNames);
    }
}