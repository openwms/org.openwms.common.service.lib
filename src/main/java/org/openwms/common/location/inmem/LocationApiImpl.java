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
package org.openwms.common.location.inmem;

import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationApi;
import org.openwms.common.location.api.LocationVO;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationApiImpl is a Spring managed transactional Service that is activated in case
 * of non-microservice deployments when the Spring Profile INMEM is activated.
 *
 * @author Heiko Scherrer
 */
@Profile("INMEM")
@TxService
class LocationApiImpl implements LocationApi {

    private final LocationService locationService;
    private final ErrorCodeTransformers.LocationStateIn stateIn;
    private final ErrorCodeTransformers.LocationStateOut stateOut;
    private final BeanMapper mapper;

    LocationApiImpl(LocationService locationService, ErrorCodeTransformers.LocationStateIn stateIn, ErrorCodeTransformers.LocationStateOut stateOut, BeanMapper mapper) {
        this.locationService = locationService;
        this.stateIn = stateIn;
        this.stateOut = stateOut;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocationVO> findLocationByCoordinate(String locationPK) {
        if (!LocationPK.isValid(locationPK)) {
            throw new IllegalArgumentException(format("Invalid location [%s]", locationPK));
        }
        Location location = locationService.findByLocationId(LocationPK.fromString(locationPK)).orElseThrow(() -> new NotFoundException(format("No Location with locationPk [%s] found", locationPK)));
        return Optional.ofNullable(mapper.map(location, LocationVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocationVO> findLocationByPlcCode(String plcCode) {
        Location location = locationService.findByPlcCode(plcCode).orElseThrow(() -> new NotFoundException(format("No Location with PLC Code [%s] found", plcCode)));
        return Optional.ofNullable(mapper.map(location, LocationVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationVO> findLocationsForLocationGroups(List<String> locationGroupNames) {
        List<Location> locations = locationService.findAllOf(locationGroupNames);
        return mapper.map(locations, LocationVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateState(String pKey, ErrorCodeVO errorCode) {
        locationService.changeState(pKey, stateIn, stateOut, errorCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationVO> findLocations(
            String area,
            String aisle,
            String x,
            String y,
            String z) {
        List<Location> craneList = locationService.findLocations(LocationPK.newBuilder().area(area).aisle(aisle).x(x).y(y).z(z).build());
        return mapper.map(craneList, LocationVO.class);
    }
}
