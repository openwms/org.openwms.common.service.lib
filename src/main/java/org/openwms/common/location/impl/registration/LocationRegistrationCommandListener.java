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
package org.openwms.common.location.impl.registration;

import org.ameba.app.SpringProfiles;
import org.openwms.common.location.api.commands.LocationReplicaRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * A LocationRegistrationCommandListener registers service instances that needs to be considered as voters on Location changes.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class LocationRegistrationCommandListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationRegistrationCommandListener.class);
    private final RegistrationService registrationService;

    LocationRegistrationCommandListener(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @RabbitListener(queues = "${owms.commands.common.registration.queue-name}")
    public void onCommand(@Payload LocationReplicaRegistration command, @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            switch (routingKey) {
                case "common.registration.command.in.register.loc" -> registrationService.register(command);
                case "common.registration.command.in.unregister.loc" -> registrationService.unregister(command);
                default -> throw new IllegalArgumentException("Unexpected routingKey in LocationReplicaRegistration [%s]".formatted(routingKey));
            }
        } catch (Exception e) {
            LOGGER.error("Processing command rejected [{}]", command);
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
