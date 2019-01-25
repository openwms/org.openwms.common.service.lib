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

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.ServiceLayerException;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.api.TUCommand;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

import static java.lang.String.format;

/**
 * A TransportUnitCommandHandler.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class TransportUnitCommandHandler {

    private final TransportUnitService service;

    TransportUnitCommandHandler(TransportUnitService service) {
        this.service = service;
    }

    @Measured
    @RabbitListener(queues = "common.commands")
    void handle(@Payload TUCommand command) {
        try {
            switch(command.getType()) {
                case CHANGE_ACTUAL_LOCATION:
                    service.moveTransportUnit(Barcode.of(command.getId()), LocationPK.fromString(command.getActualLocation()));
                    break;
                default:
                    throw new ServiceLayerException(format("Operation [%s] of TUCommand not supported", command.getType()));
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
