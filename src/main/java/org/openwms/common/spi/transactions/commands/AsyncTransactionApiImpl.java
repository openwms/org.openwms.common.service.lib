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
package org.openwms.common.spi.transactions.commands;

import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A AsyncTransactionApiImpl.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class AsyncTransactionApiImpl implements AsyncTransactionApi {

    private final AmqpTemplate template;
    private final String exchangeName;

    AsyncTransactionApiImpl(AmqpTemplate template,
            @Value("${owms.commands.transactions.tx.exchange-name}") String exchangeName) {
        this.template = template;
        this.exchangeName = exchangeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void process(TransactionCommand command) {
        template.convertAndSend(exchangeName, "common.tx.command.in.create", command);
    }
}
