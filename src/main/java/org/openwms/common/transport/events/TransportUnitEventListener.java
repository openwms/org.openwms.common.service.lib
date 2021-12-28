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
package org.openwms.common.transport.events;

import org.ameba.http.identity.IdentityContextHolder;
import org.ameba.i18n.Translator;
import org.openwms.common.spi.transactions.TransactionBuilder;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.openwms.common.spi.transactions.commands.TransactionCommand;
import org.openwms.common.transport.TransportUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.openwms.common.CommonMessageCodes.MSG_TU_MOVED;

/**
 * A TransportUnitEventListener.
 *
 * @author Heiko Scherrer
 */
@RefreshScope
@Component
class TransportUnitEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("BUSINESS_EVENTS");
    private final Translator translator;
    private final AsyncTransactionApi transactionApi;

    TransportUnitEventListener(Translator translator, AsyncTransactionApi transactionApi) {
        this.translator = translator;
        this.transactionApi = transactionApi;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onEvent(TransportUnitEvent event) {
        switch (event.getType()) {
            case MOVED:
                var tu = (TransportUnit) event.getSource();
                var description = translator.translate(MSG_TU_MOVED,
                        tu.getBarcode().getValue(),
                        event.getPreviousLocation().getLocationId(),
                        tu.getActualLocation().getLocationId()
                );
                LOGGER.info(description);
                transactionApi.process(TransactionCommand.of(TransactionCommand.Type.CREATE,
                    createDefaultBuilder().withType(MSG_TU_MOVED)
                            .withDescription(description)
                            .withDetail("transportUnitBK", tu.getBarcode().getValue())
                            .withDetail("previousLocation", event.getPreviousLocation().getLocationId().toString())
                            .withDetail("previousLocationErpCode", event.getPreviousLocation().getErpCode())
                            .withDetail("previousLocationPlcCode", event.getPreviousLocation().getPlcCode())
                            .withDetail("actualLocation", tu.getActualLocation().getLocationId().toString())
                            .withDetail("actualLocationErpCode", tu.getActualLocation().getErpCode())
                            .withDetail("actualLocationPlcCode", tu.getActualLocation().getPlcCode())
                            .build()
                ));
                break;
            default:
        }
    }

    private TransactionBuilder createDefaultBuilder() {
        return TransactionBuilder.aTransactionVO()
                .withCreatedByUser(IdentityContextHolder.getCurrentIdentity())
                .withSender("common-service");
    }
}
