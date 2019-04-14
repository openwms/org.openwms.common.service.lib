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
package org.openwms.common.transport.commands;

import org.ameba.annotation.TxService;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.UnitError;
import org.openwms.common.transport.api.commands.MessageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A MessageCommandHandler handles {@link MessageCommand}s.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @see MessageCommand
 */
@TxService
public class MessageCommandHandlerImpl implements MessageCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandHandlerImpl.class);
    private final TransportUnitService service;
    private final ApplicationContext ctx;

    MessageCommandHandlerImpl(TransportUnitService service, ApplicationContext ctx) {
        this.service = service;
        this.ctx = ctx;
    }

    public void handle(MessageCommand command) {
        switch(command.getType()) {
            case ADD_TO_TU:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got command to ADD a Message to the TransportUnit with id [{}]", command.getTransportUnitId());
                }
                TransportUnit tu = service.findByBarcode(Barcode.of(command.getTransportUnitId()), Boolean.FALSE);
                tu.addError(UnitError.newBuilder().errorText(command.getMessageText()).errorNo(command.getMessageNumber()).build());
                break;
            default:
                LOGGER.debug("MessageCommand [{}] not supported", command.getType());
        }
    }
}
