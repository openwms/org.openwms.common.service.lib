/*
 * Copyright 2005-2021 the original author or authors.
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
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitState;
import org.openwms.common.transport.api.ValidationGroups;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.common.transport.impl.TransportUnitMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import javax.validation.Validator;

import static org.ameba.system.ValidationUtil.validate;
import static org.openwms.common.transport.api.commands.TUCommand.Type.UPDATE_CACHE;

/**
 * A TransportUnitCommandHandler is just a handler or an adapter implementation that is used by a listener and passes incoming {@link TUCommand}s
 * to the internal ApplicationContext.
 *
 * @author Heiko Scherrer
 * @see TUCommand
 */
@Validated
@TxService
class TransportUnitCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitCommandHandler.class);
    private final TransportUnitMapper mapper;
    private final Validator validator;
    private final BarcodeGenerator generator;
    private final TransportUnitService service;
    private final ApplicationContext ctx;

    TransportUnitCommandHandler(TransportUnitMapper mapper, Validator validator, BarcodeGenerator generator, TransportUnitService service,
            ApplicationContext ctx) {
        this.mapper = mapper;
        this.validator = validator;
        this.generator = generator;
        this.service = service;
        this.ctx = ctx;
    }

    public void handle(TUCommand command) {
        switch(command.getType()) {
            case CHANGE_ACTUAL_LOCATION:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to MOVE TransportUnit with id [{}] to [{}]", command.getTransportUnit().getBarcode(), command.getTransportUnit().getActualLocation());
                }
                service.moveTransportUnit(generator.convert(command.getTransportUnit().getBarcode()), LocationPK.fromString(command.getTransportUnit().getActualLocation()));
                break;
            case CHANGE_TARGET:
                validate(validator, command, ValidationGroups.TransportUnit.ChangeTarget.class);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to CHANGE the target of the TransportUnit with id [{}] to [{}]", command.getTransportUnit().getBarcode(), command.getTransportUnit().getTargetLocation());
                }
                service.changeTarget(generator.convert(command.getTransportUnit().getBarcode()), command.getTransportUnit().getTargetLocation());
                break;
            case REMOVE:
                validate(validator, command, ValidationGroups.TransportUnit.Remove.class);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to REMOVE TransportUnit with pKey [{}]", command.getTransportUnit().getpKey());
                }
                ctx.publishEvent(command);
                break;
            case REQUEST:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got a request command for a TransportUnit");
                }
                validate(validator, command, ValidationGroups.TransportUnit.Request.class);
                TransportUnit tu = service.findByPKey(command.getTransportUnit().getpKey());
                TransportUnitMO mo = mapper.convertToMO(tu);
                ctx.publishEvent(TUCommand.newBuilder(UPDATE_CACHE).withTransportUnit(mo).build());
                break;
            case CREATE:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got a command to create a TransportUnit");
                }
                validate(validator, command, ValidationGroups.TransportUnit.Create.class);
                tu = service.create(
                        command.getTransportUnit().getBarcode(),
                        command.getTransportUnit().getTransportUnitType().getType(),
                        command.getTransportUnit().getActualLocation(),
                        false
                );
                mo = mapper.convertToMO(tu);
                ctx.publishEvent(
                        TUCommand.newBuilder(UPDATE_CACHE).withTransportUnit(mo).build()
                );
                break;
            case BLOCK:
                mo = command.getTransportUnit();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got a command to BLOCK a TransportUnit");
                }
                service.setState(mo.getBarcode(), TransportUnitState.valueOf(mo.getState()));
                break;
            default:
                LOGGER.error("TUCommand [{}] not supported", command.getType());
        }
    }
}
