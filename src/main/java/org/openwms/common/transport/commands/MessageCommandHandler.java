package org.openwms.common.transport.commands;

import org.openwms.common.transport.api.commands.MessageCommand;

public interface MessageCommandHandler {

    void handle(MessageCommand command);
}
