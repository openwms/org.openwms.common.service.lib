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
package org.openwms.common.transport;

import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.location.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
            transportUnitTypeRepository
                    .findByType(transportUnitType.getType())
                    .ifPresent(transportUnitTypeRepository::delete);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnitType save(TransportUnitType transportUnitType) {
        TransportUnitType tut = transportUnitTypeRepository.save(transportUnitType);
        LOGGER.debug("Save a TransportUnitType, list of typePlacingRules:" + tut.getTypePlacingRules().size());
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
        LOGGER.debug("Found type " + type);
        rules.addAll(type.getTypePlacingRules());
        rules.addAll(type.getTypeStackingRules());
        LOGGER.debug("returning a list with items" + rules.size());
        return rules;
    }
}