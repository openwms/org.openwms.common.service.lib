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
package org.openwms.common.location.events;

import org.ameba.app.SpringProfiles;
import org.openwms.common.location.api.commands.RevokeLocationRemoveCommand;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A DeletionFailedEventListener.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class DeletionFailedEventListener {

    private final String exchangeName;
    private final AmqpTemplate amqpTemplate;

    DeletionFailedEventListener(@Value("${owms.events.common.lg.exchange-name}") String exchangeName, AmqpTemplate amqpTemplate) {
        this.exchangeName = exchangeName;
        this.amqpTemplate = amqpTemplate;
    }

    @EventListener
    public void onRevokeLocationRemoveCommand(DeletionFailedEvent event) {
        amqpTemplate.convertAndSend(exchangeName, "loc.event.deletion-rollback", new RevokeLocationRemoveCommand((event.getSource())));
    }
}
