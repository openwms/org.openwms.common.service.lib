/*
 * Copyright 2005-2020 the original author or authors.
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

import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.api.events.LocationEvent;
import org.openwms.common.location.api.events.LocationGroupEvent;
import org.openwms.common.location.api.messages.LocationGroupMO;
import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;

import static java.lang.String.format;

/**
 * A LocationGroupEventPropagator.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class LocationGroupEventPropagator {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;
    private final BeanMapper mapper;

    LocationGroupEventPropagator(AmqpTemplate amqpTemplate, @Value("${owms.events.common.lg.exchange-name}") String exchangeName, BeanMapper mapper) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
        this.mapper = mapper;
    }

    @PostConstruct
    void onStartup() {
        amqpTemplate.convertAndSend(exchangeName, "lg.event.boot", LocationGroupEvent.boot());
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(LocationGroupEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "lg.event.created", mapper.map(event.getSource(), LocationGroupMO.class));
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "lg.event.changed", mapper.map(event.getSource(), LocationGroupMO.class));
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "lg.event.deleted", mapper.map(event.getSource(), LocationGroupMO.class));
                break;
            case STATE_CHANGE:
                amqpTemplate.convertAndSend(exchangeName, "lg.event.state-changed", mapper.map(event.getSource(), LocationGroupMO.class));
                break;
            default:
                throw new UnsupportedOperationException(format("Eventtype [%s] currently not supported", event.getType()));
        }
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onLocationEvent(LocationEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.created", mapper.map(event.getSource(), LocationMO.class));
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.changed", mapper.map(event.getSource(), LocationMO.class));
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.deleted", mapper.map(event.getSource(), LocationMO.class));
                break;
            case STATE_CHANGE:
                amqpTemplate.convertAndSend(exchangeName, "loc.event.state-changed", mapper.map(event.getSource(), LocationMO.class));
                break;
            default:
                throw new UnsupportedOperationException(format("Eventtype [%s] currently not supported", event.getType()));
        }
    }
}
