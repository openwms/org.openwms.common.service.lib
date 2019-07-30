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
package org.openwms.common.transport.events;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationService;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import static java.lang.String.format;

/**
 * A TransportUnitEventListener.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@TxService
class TransportUnitEventListener {

    private final TransportUnitService service;
    private final LocationService locationService;

    TransportUnitEventListener(TransportUnitService service, LocationService locationService) {
        this.service = service;
        this.locationService = locationService;
    }

    @Measured
    @RabbitListener(queues = "${owms.events.common.tu.queue-name}")
    void handle(@Payload TransportUnitMO msg, @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            if ("tu.event.change.target".equals(routingKey)) {
                Location location = locationService.findByLocationId(msg.getTargetLocation())
                        .orElseThrow(() -> new NotFoundException(format("Location with locationId [%s] not found", msg.getTargetLocation())));

                Barcode barcode = Barcode.of(msg.getBarcode());
                TransportUnit saved = service.findByBarcode(barcode);
                saved.setTargetLocation(location);
                service.update(barcode, saved);
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
