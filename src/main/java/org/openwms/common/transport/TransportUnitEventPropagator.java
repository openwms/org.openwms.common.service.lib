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
package org.openwms.common.transport;

import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static java.lang.String.format;

/**
 * A AmqpEventPropagator.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class TransportUnitEventPropagator {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;

    TransportUnitEventPropagator(AmqpTemplate amqpTemplate, @Value("${owms.events.common.tu.exchange-name}") String exchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(TransportUnitEvent event) {
        switch (event.getType()) {
            case CREATED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.created", event.getSource());
                break;
            case CHANGED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.changed", event.getSource());
                break;
            case DELETED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.deleted", event.getSource());
                break;
            case MOVED:
                amqpTemplate.convertAndSend(exchangeName, "tu.event.moved", event.getSource());
                break;
            default:
                throw new UnsupportedOperationException(format("Eventtype [%s] currently not supported", event.getType()));
        }
    }
}
