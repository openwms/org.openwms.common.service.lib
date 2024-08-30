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
package org.openwms.common.transport.reservation;

import org.ameba.annotation.Measured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static org.openwms.core.SpringProfiles.ASYNCHRONOUS_PROFILE;
import static org.openwms.core.SpringProfiles.NOT_MANAGED;

/**
 * A ReservationMessageListener acknowledges a {@code Reservation} as soon as a {@code ShippingOrderPositionSplit} has been created.
 *
 * @author Heiko Scherrer
 */
@Profile(ASYNCHRONOUS_PROFILE + " && " + NOT_MANAGED)
@Component
class ReservationMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationMessageListener.class);
    private final ReservationService reservationService;

    ReservationMessageListener(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Measured
    @RabbitListener(queues = "${owms.events.shipping.split.queue-name}")
    public void handle(@Payload SplitMO mo, @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            reservationService.acknowledgeReservation(
                    mo.getReservationId(),
                    "SOPS::" + mo.getShippingOrderPositionPKey() + "::" + mo.getSplitNo()
            );
        } catch (Exception e) {
            LOGGER.error("Acknowledging split rejected [{}]", mo);
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
