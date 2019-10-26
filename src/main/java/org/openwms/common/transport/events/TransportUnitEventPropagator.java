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
package org.openwms.common.transport.events;

import org.ameba.mapping.BeanMapper;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static java.lang.String.format;

/**
 * A TransportUnitEventPropagator.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class TransportUnitEventPropagator {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;
    private final BeanMapper mapper;

    TransportUnitEventPropagator(
            AmqpTemplate amqpTemplate,
            @Value("${owms.events.common.tu.exchange-name}") String exchangeName,
            BeanMapper mapper) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
        this.mapper = mapper;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(TransportUnitEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.created", mapper.map(event.getSource(), TransportUnitMO.class));
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.changed", mapper.map(event.getSource(), TransportUnitMO.class));
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.deleted", mapper.map(event.getSource(), TransportUnitMO.class));
                break;
            case STATE_CHANGE:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.state-changed", mapper.map(event.getSource(), TransportUnitMO.class));
                break;
            case MOVED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.moved."+event.getActualLocation().getLocationId(), mapper.map(event.getSource(), TransportUnitMO.class));
                break;
            default:
                throw new UnsupportedOperationException(format("Eventtype [%s] not supported", event.getType()));
        }
    }
}
