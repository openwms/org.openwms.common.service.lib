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
package org.openwms.common.transport.events;

import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.impl.TransportUnitTypeMapper;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A TransportUnitTypeEventPropagator is a Spring managed component to publish {@link TransportUnitTypeEvent}s over AQMP. This component is
 * only active with the {@value SpringProfiles#ASYNCHRONOUS_PROFILE} profile.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@RefreshScope
@Component
class TransportUnitTypeEventPropagator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitTypeEventPropagator.class);
    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;
    private final TransportUnitTypeMapper mapper;

    TransportUnitTypeEventPropagator(
            AmqpTemplate amqpTemplate,
            @Value("${owms.events.common.tut.exchange-name}") String exchangeName,
            TransportUnitTypeMapper mapper) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
        this.mapper = mapper;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(TransportUnitTypeEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "tut.event.created", mapper.convertToMO((TransportUnitType) event.getSource()));
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "tut.event.changed", mapper.convertToMO((TransportUnitType) event.getSource()));
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "tut.event.deleted", mapper.convertToMO((TransportUnitType) event.getSource()));
                break;
            default:
                LOGGER.warn("TransportUnitTypeEvent [{}] not supported", event.getType());
        }
    }
}
