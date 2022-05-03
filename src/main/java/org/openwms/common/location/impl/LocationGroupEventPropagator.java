/*
 * Copyright 2005-2022 the original author or authors.
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

import org.openwms.common.location.Location;
import org.openwms.common.location.LocationGroup;
import org.openwms.common.location.LocationGroupMapper;
import org.openwms.common.location.LocationMapper;
import org.openwms.common.location.api.events.LocationEvent;
import org.openwms.common.location.api.events.LocationGroupEvent;
import org.openwms.common.location.api.messages.LocationGroupMO;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import javax.validation.Validator;

import static java.lang.String.format;
import static org.ameba.system.ValidationUtil.validate;

/**
 * A LocationGroupEventPropagator.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@RefreshScope
@Component
class LocationGroupEventPropagator {

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
            // Its fine if the event broker is not available on startup
        }
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(LocationGroupEvent event) {
        switch (event.getType()) {
            case CREATED -> amqpTemplate.convertAndSend(exchangeName, "lg.event.created", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            case CHANGED -> amqpTemplate.convertAndSend(exchangeName, "lg.event.changed", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            case DELETED -> amqpTemplate.convertAndSend(exchangeName, "lg.event.deleted", locationGroupMapper.convertToMO((LocationGroup) event.getSource()));
            case STATE_CHANGE -> {
                LocationGroupMO msg = locationGroupMapper.convertToMO((LocationGroup) event.getSource());
                validate(validator, msg);
                amqpTemplate.convertAndSend(exchangeName, "lg.event.state-changed", msg);
            }
            default -> throw new UnsupportedOperationException(format("LocationGroupEvent [%s] currently not supported", event.getType()));
        }
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onLocationEvent(LocationEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.created", locationMapper.convertToMO((Location) event.getSource()));
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.changed", locationMapper.convertToMO((Location) event.getSource()));
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.deleted", locationMapper.convertToMO((Location) event.getSource()));
                break;
            case STATE_CHANGE:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.state-changed", locationMapper.convertToMO((Location) event.getSource()));
                break;
            default:
                throw new UnsupportedOperationException(format("LocationEvent [%s] currently not supported", event.getType()));
        }
    }
}
