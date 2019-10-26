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
package org.openwms.common.transport.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.location.LocationType;
import org.openwms.common.transport.Rule;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.TransportUnitTypeService;
import org.openwms.common.transport.TypePlacingRule;
import org.openwms.common.transport.events.TransportUnitTypeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.openwms.common.transport.events.TransportUnitTypeEvent.TransportUnitTypeEventType.CHANGED;
import static org.openwms.common.transport.events.TransportUnitTypeEvent.TransportUnitTypeEventType.CREATED;

/**
 * A TransportUnitTypeServiceImpl is a Spring managed bean that deals with TransportUnitTypes.
 *
 * @author Heiko Scherrer
 */
@TxService
class TransportUnitTypeServiceImpl implements TransportUnitTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitTypeServiceImpl.class);
    private final TransportUnitTypeRepository transportUnitTypeRepository;
    private final ApplicationEventPublisher publisher;

    TransportUnitTypeServiceImpl(
            TransportUnitTypeRepository transportUnitTypeRepository,
            ApplicationEventPublisher publisher) {
        this.transportUnitTypeRepository = transportUnitTypeRepository;
        this.publisher = publisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<TransportUnitType> findByType(String type) {
        return transportUnitTypeRepository.findByType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public List<TransportUnitType> findAll() {
        return transportUnitTypeRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnitType create(@NotNull TransportUnitType transportUnitType) {
        TransportUnitType created = transportUnitTypeRepository.save(transportUnitType);
        publisher.publishEvent(TransportUnitTypeEvent.newBuilder()
                .tut(created)
                .type(CREATED)
                .build());
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void deleteType(TransportUnitType... transportUnitTypes) {
        for (TransportUnitType transportUnitType : transportUnitTypes) {
            transportUnitTypeRepository.findByType(transportUnitType.getType()).ifPresent(transportUnitTypeRepository::delete);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnitType save(TransportUnitType transportUnitType) {
        TransportUnitType tut = transportUnitTypeRepository.save(transportUnitType);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Save a TransportUnitType with a list of TypePlacingRules [{}]", tut.getTypePlacingRules().size());
        }
        publisher.publishEvent(TransportUnitTypeEvent.newBuilder()
                .tut(tut)
                .type(CHANGED)
                .build());
        return tut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnitType updateRules(@NotEmpty String type, List<LocationType> newAssigned, List<LocationType> newNotAssigned) {
        TransportUnitType tut = transportUnitTypeRepository.findByType(type).orElseThrow(() -> new NotFoundException(format("TransportUnitType with type [%s] does not exist", type)));
        if (newAssigned != null && !newAssigned.isEmpty()) {
            for (LocationType locationType : newAssigned) {
                if (tut.getTypePlacingRules()
                        .stream()
                        .map(TypePlacingRule::getAllowedLocationType)
                        .noneMatch(lt -> lt.equals(locationType))
                ) {
                    TypePlacingRule newRule = new TypePlacingRule(tut, locationType);
                    tut.addTypePlacingRule(newRule);
                }
            }
        }
        if (newNotAssigned != null && !newNotAssigned.isEmpty()) {
            for (LocationType locationType : newNotAssigned) {
                tut.getTypePlacingRules()
                        .forEach(tpr -> {
                            if (tpr.getAllowedLocationType().equals(locationType)) {
                                tut.removeTypePlacingRule(tpr);
                            }
                        });
            }
        }
        return transportUnitTypeRepository.save(tut);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<Rule> loadRules(@NotEmpty String transportUnitType) {
        TransportUnitType type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(() -> new NotFoundException(format("TransportUnitType with type [%s] does not exist", transportUnitType)));
        List<Rule> rules = new ArrayList<>(type.getTypePlacingRules());
        rules.addAll(type.getTypeStackingRules());
        return rules;
    }
}