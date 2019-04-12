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
package org.openwms.common.location.inmem;

import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupService;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupApi;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationGroupVO;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * A LocationGroupApiImpl is a Spring managed transactional Service that is activated in
 * case of non-microservice deployments when the Spring Profile INMEM is activated.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("INMEM")
@TxService
class LocationGroupApiImpl implements LocationGroupApi {

    private final LocationGroupService locationGroupService;
    private final BeanMapper mapper;
    private final ErrorCodeTransformers.GroupStateIn groupStateIn;
    private final ErrorCodeTransformers.GroupStateOut groupStateOut;

    LocationGroupApiImpl(LocationGroupService locationGroupService, BeanMapper mapper, ErrorCodeTransformers.GroupStateIn groupStateIn, ErrorCodeTransformers.GroupStateOut groupStateOut) {
        this.locationGroupService = locationGroupService;
        this.mapper = mapper;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocationGroupVO> findByName(String name) {
        Optional<LocationGroup> opt = locationGroupService.findByName(name);
        LocationGroup locationGroup = opt.orElseThrow(() -> new NotFoundException(format("LocationGroup with name [%s] does not exist", name)));
        LocationGroupVO result = mapper.map(locationGroup, LocationGroupVO.class);
        return Optional.of(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationGroupVO> findByNames(List<String> names) {
        List<LocationGroup> locationGroups = locationGroupService.findByNames(names);
        return mapper.map(locationGroups, LocationGroupVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationGroupVO> findAll() {
        List<LocationGroup> all = locationGroupService.findAll();
        return all == null ? Collections.emptyList() : mapper.map(all, LocationGroupVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateState(String name, ErrorCodeVO errorCode) {
        Optional<LocationGroupState> gsIn = groupStateIn.transform(errorCode.getErrorCode());
        Optional<LocationGroupState> gsOut = groupStateOut.transform(errorCode.getErrorCode());
        locationGroupService.changeGroupStates(name, gsIn, gsOut);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String id, LocationGroupState stateIn, LocationGroupState stateOut) {
        locationGroupService.changeGroupState(id, stateIn, stateOut);
    }
}
