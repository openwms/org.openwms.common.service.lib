/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.location;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Location location = locationRepository.findOne(id);
        if (null == location) {
            throw new ServiceLayerException(format("Location with pk [%s] not found, probably it was removed before", id));
        }
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
        locationTypes.stream()
                .map(locationType -> locationTypeRepository.findOne(locationType.getPk()))
                .forEach(locationTypeRepository::delete);
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
    public Location findByLocationId(LocationPK locationPK) {
        return locationRepository.findByLocationId(locationPK).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", locationPK), null));
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
    @Measured
    public List<Location> findAllOf(List<String> locationGroupNames) {
        return locationRepository.findByLocationGroup_Name(locationGroupNames);
    }
}