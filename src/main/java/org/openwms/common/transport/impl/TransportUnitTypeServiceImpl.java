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
package org.openwms.common.transport.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
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
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.openwms.common.CommonMessageCodes.TRANSPORT_UNIT_TYPE_NOT_FOUND_BY_PKEY;
import static org.openwms.common.transport.events.TransportUnitTypeEvent.TransportUnitTypeEventType.CHANGED;
import static org.openwms.common.transport.events.TransportUnitTypeEvent.TransportUnitTypeEventType.CREATED;
import static org.openwms.common.transport.events.TransportUnitTypeEvent.TransportUnitTypeEventType.DELETED;

/**
 * A TransportUnitTypeServiceImpl is a Spring managed bean that deals with TransportUnitTypes.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class TransportUnitTypeServiceImpl implements TransportUnitTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitTypeServiceImpl.class);
    private final Translator translator;
    private final TransportUnitTypeRepository transportUnitTypeRepository;
    private final ApplicationEventPublisher publisher;

    TransportUnitTypeServiceImpl(
            Translator translator, TransportUnitTypeRepository transportUnitTypeRepository,
            ApplicationEventPublisher publisher) {
        this.translator = translator;
        this.transportUnitTypeRepository = transportUnitTypeRepository;
        this.publisher = publisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnitType findByPKey(String pKey) {
        return findInternal(pKey);
    }

    private TransportUnitType findInternal(String pKey) {
        return transportUnitTypeRepository
                .findBypKey(pKey)
                .orElseThrow(() -> new NotFoundException(translator, TRANSPORT_UNIT_TYPE_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(readOnly = true)
    public Optional<TransportUnitType> findByType(@NotBlank String type) {
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
        var created = transportUnitTypeRepository.save(transportUnitType);
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
        for (var transportUnitType : transportUnitTypes) {
            transportUnitTypeRepository.findByType(transportUnitType.getType()).ifPresent(tut -> {
                transportUnitTypeRepository.delete(tut);
                publisher.publishEvent(TransportUnitTypeEvent.newBuilder()
                        .tut(tut)
                        .type(DELETED)
                        .build());
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TransportUnitType save(@NotNull TransportUnitType transportUnitType) {
        var tut = transportUnitTypeRepository.save(transportUnitType);
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
    public TransportUnitType updateRules(@NotEmpty @NotEmpty String type,
                                         @NotNull List<LocationType> newAssigned,
                                         @NotNull List<LocationType> newNotAssigned) {
        var tut = transportUnitTypeRepository.findByType(type)
                .orElseThrow(() -> new NotFoundException(translator,
                        CommonMessageCodes.TRANSPORT_UNIT_TYPE_NOT_FOUND,
                        new String[]{type}, type));
        if (!newAssigned.isEmpty()) {
            for (var locationType : newAssigned) {
                if (tut.getTypePlacingRules()
                        .stream()
                        .map(TypePlacingRule::getAllowedLocationType)
                        .noneMatch(lt -> lt.equals(locationType))
                ) {
                    if (locationType.isNew()) {
                        throw new ServiceLayerException(format("LocationType [%s] does not exist and must be persisted before adding it as a rule", locationType.getType()));
                    }
                    var newRule = new TypePlacingRule(tut, locationType);
                    tut.addTypePlacingRule(newRule);
                }
            }
        }
        if (!newNotAssigned.isEmpty()) {
            for (var locationType : newNotAssigned) {
                tut.removeTypePlacingRules(locationType);
            }
        }
        tut = transportUnitTypeRepository.save(tut);
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
    public List<Rule> loadRules(@NotEmpty String transportUnitType) {
        var type = transportUnitTypeRepository.findByType(transportUnitType).orElseThrow(() -> new NotFoundException(format("TransportUnitType with type [%s] does not exist", transportUnitType)));
        var rules = new ArrayList<Rule>(type.getTypePlacingRules());
        rules.addAll(type.getTypeStackingRules());
        return rules;
    }
}