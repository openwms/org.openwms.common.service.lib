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
package org.openwms.common.transport.commands;

import org.ameba.annotation.TxService;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.commands.TUCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A TransportUnitCommandHandler passes incoming {@link TUCommand}s to the internal
 * ApplicationContext.
 *
 * @author Heiko Scherrer
 * @see TUCommand
 */
@TxService
class TransportUnitCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitCommandHandler.class);
    private final TransportUnitService service;
    private final ApplicationContext ctx;

    TransportUnitCommandHandler(TransportUnitService service, ApplicationContext ctx) {
        this.service = service;
        this.ctx = ctx;
    }

    public void handle(TUCommand command) {
        switch(command.getType()) {
            case CHANGE_ACTUAL_LOCATION:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to MOVE TransportUnit with id [{}] to [{}]", command.getTransportUnit().getBarcode(), command.getTransportUnit().getActualLocation());
                }
                service.moveTransportUnit(Barcode.of(command.getTransportUnit().getBarcode()), LocationPK.fromString(command.getTransportUnit().getActualLocation()));
                break;
            case CHANGE_TARGET:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to CHANGE the target of the TransportUnit with id [{}] to [{}]", command.getTransportUnit().getBarcode(), command.getTransportUnit().getTargetLocation());
                }
                service.changeTarget(Barcode.of(command.getTransportUnit().getBarcode()), command.getTransportUnit().getTargetLocation());
                break;
            case REMOVE:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to REMOVE TransportUnit with id [{}]", command.getTransportUnit().getBarcode());
                }
                ctx.publishEvent(command);
                break;
            default:
                LOGGER.debug("TUCommand [{}] not supported", command.getType());
        }
    }
}
