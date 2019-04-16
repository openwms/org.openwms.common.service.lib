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
package org.openwms.common.transport.impl;

import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.location.LocationType;
import org.openwms.common.transport.Rule;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.TransportUnitTypeService;
import org.openwms.common.transport.TypePlacingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A TransportUnitTypeServiceImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class TransportUnitTypeServiceImpl implements TransportUnitTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitTypeServiceImpl.class);
    private final TransportUnitTypeRepository transportUnitTypeRepository;

    public TransportUnitTypeServiceImpl(TransportUnitTypeRepository transportUnitTypeRepository) {
        this.transportUnitTypeRepository = transportUnitTypeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<TransportUnitType> findByType(String type) {
        return transportUnitTypeRepository.findByType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransportUnitType> findAll() {
        return transportUnitTypeRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnitType create(TransportUnitType transportUnitType) {
        transportUnitTypeRepository.save(transportUnitType);
        return transportUnitTypeRepository.save(transportUnitType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteType(TransportUnitType... transportUnitTypes) {
        for (TransportUnitType transportUnitType : transportUnitTypes) {
            transportUnitTypeRepository.findByType(transportUnitType.getType()).ifPresent(transportUnitTypeRepository::delete);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnitType save(TransportUnitType transportUnitType) {
        TransportUnitType tut = transportUnitTypeRepository.save(transportUnitType);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Save a TransportUnitType, list of typePlacingRules [{}]", tut.getTypePlacingRules().size());
        }
        return tut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnitType updateRules(String type, List<LocationType> newAssigned, List<LocationType> newNotAssigned) {

        TransportUnitType tut = transportUnitTypeRepository.findByType(type).orElseThrow(NotFoundException::new);
        boolean found = false;
        if (newAssigned != null && !newAssigned.isEmpty()) {
            for (LocationType locationType : newAssigned) {
                for (TypePlacingRule rule : tut.getTypePlacingRules()) {
                    if (rule.getAllowedLocationType() == locationType) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    TypePlacingRule newRule = new TypePlacingRule(tut, locationType);
                    tut.addTypePlacingRule(newRule);
                }
            }
        }

        if (newAssigned != null && !newAssigned.isEmpty()) {
            for (LocationType locationType : newNotAssigned) {
                for (TypePlacingRule rule : tut.getTypePlacingRules()) {
                    if (rule.getAllowedLocationType() == locationType) {
                        tut.removeTypePlacingRule(rule);
                        break;
                    }
                }
            }
        }

        return transportUnitTypeRepository.save(tut);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Rule> loadRules(String transportUnitType) {
        TransportUnitType type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(NotFoundException::new);
        List<Rule> rules = new ArrayList<>();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found type [{}]", type);
        }
        rules.addAll(type.getTypePlacingRules());
        rules.addAll(type.getTypeStackingRules());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning a list with [{}] items", rules.size());
        }
        return rules;
    }
}