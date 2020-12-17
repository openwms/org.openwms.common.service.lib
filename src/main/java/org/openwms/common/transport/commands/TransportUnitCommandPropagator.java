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
package org.openwms.common.transport.commands;

import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * A TransportUnitCommandPropagator propagates {@link TUCommand}s send by this service
 * out to other services over an AMQP exchange.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@RefreshScope
@Component
class TransportUnitCommandPropagator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitCommandPropagator.class);
    private final AmqpTemplate amqpTemplate;
    private final Validator validator;
    private final String exchangeName;

    TransportUnitCommandPropagator(AmqpTemplate amqpTemplate, Validator validator,
            @Value("${owms.commands.common.tu.exchange-name}") String exchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.validator = validator;
        this.exchangeName = exchangeName;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(TUCommand command) {
        switch(command.getType()) {
            case REMOVING:
                Set<ConstraintViolation<TUCommand>> violations = validator.validate(command);
                if (!violations.isEmpty()) {
                    throw new ValidationException(violations.iterator().next().getMessage());
                }
                if (command.getType() == TUCommand.Type.REMOVING) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Sending REMOVING command to announce the TransportUnit [{}] is going to be removed", command.getTransportUnit().getpKey());
                    }
                    amqpTemplate.convertAndSend(
                            exchangeName,
                            "common.tu.command.out.removing",
                            command
                    );
                }
                break;
            case UPDATE_CACHE:
                amqpTemplate.convertAndSend(
                        exchangeName,
                        "common.tu.command.out.update-cache",
                        command
                );
                break;
            default:
                LOGGER.warn("Not supported command type [{}]", command.getType());
        }
    }
}
