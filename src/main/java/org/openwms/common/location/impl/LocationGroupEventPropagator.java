/*
 * Copyright 2005-2025 the original author or authors.
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

import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import org.ameba.annotation.Measured;
import org.ameba.app.SpringProfiles;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupMapper;
import org.openwms.common.location.LocationMapper;
import org.openwms.common.location.api.events.LocationEvent;
import org.openwms.common.location.api.events.LocationGroupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static java.lang.String.format;
import static org.ameba.system.ValidationUtil.validate;

/**
 * A LocationGroupEventPropagator.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.AMQP)
@RefreshScope
@Component
class LocationGroupEventPropagator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationGroupEventPropagator.class);

    private final AmqpTemplate amqpTemplate;
    private final Validator validator;
    private final String exchangeName;
    private final LocationMapper locationMapper;
    private final LocationGroupMapper locationGroupMapper;

    LocationGroupEventPropagator(AmqpTemplate amqpTemplate, Validator validator,
            @Value("${owms.events.common.lg.exchange-name}") String exchangeName, LocationMapper locationMapper, LocationGroupMapper locationGroupMapper) {
        this.amqpTemplate = amqpTemplate;
        this.validator = validator;
        this.exchangeName = exchangeName;
        this.locationMapper = locationMapper;
        this.locationGroupMapper = locationGroupMapper;
    }

    @PostConstruct
    void onStartup() {
        try {
            amqpTemplate.convertAndSend(exchangeName, "lg.event.boot", LocationGroupEvent.boot());
        } catch (Exception e) {
            // It is fine if the event broker is not available on startup
        }
    }

    @Measured
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEvent(LocationGroupEvent event) {
        switch (event.getType()) {
            case CREATED -> {
                LOGGER.info("LocationGroup successfully created [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "lg.event.created", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            }
            case CHANGED -> {
                LOGGER.info("LocationGroup successfully modified [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "lg.event.changed", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            }
            case DELETED -> {
                LOGGER.info("LocationGroup successfully deleted [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "lg.event.deleted", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            }
            case STATE_CHANGE -> {
                LOGGER.info("LocationGroup changed state [{}]", event.getSource());
                var msg = locationGroupMapper.convertToMO((LocationGroup) event.getSource());
                validate(validator, msg);
                amqpTemplate.convertAndSend(exchangeName, "lg.event.state-changed", msg);
            }
            default -> throw new UnsupportedOperationException(format("LocationGroupEvent [%s] currently not supported", event.getType()));
        }
    }

    @Measured
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onLocationEvent(LocationEvent event) {
        switch (event.getType()) {
            case CREATED -> {
                LOGGER.info("Location successfully created [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "loc.event.created", locationMapper.convertToMO((Location) event.getSource()));
            }
            case CHANGED -> {
                LOGGER.info("Location successfully modified [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "loc.event.changed", locationMapper.convertToMO((Location) event.getSource()));
            }
            case DELETED -> {
                LOGGER.info("Location successfully deleted [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "loc.event.deleted", locationMapper.convertToMO((Location) event.getSource()));
            }
            case STATE_CHANGE -> {
                LOGGER.info("Location changed state [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "loc.event.state-changed", locationMapper.convertToMO((Location) event.getSource()));
            }
            default -> throw new UnsupportedOperationException(format("LocationEvent [%s] currently not supported", event.getType()));
        }
    }
}
