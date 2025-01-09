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
package org.openwms.common.transport.impl;

import org.ameba.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitServiceImplAsyncIT.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@CommonApplicationTest
class TransportUnitServiceImplAsyncIT {

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private TransportUnitService testee;
    @MockitoBean
    private AsyncTransactionApi transactionApi;
    @Autowired
    private TransactionTemplate txTemplate;

    @BeforeEach
    void onSetup() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "%s");
        System.setProperty("owms.common.barcode.padder", "0");
    }

    @Test void shall_trigger_deletion() {
        var tu = testee.findByPKey(TestData.TU_1_PKEY);
        assertThat(tu).isNotNull();
        txTemplate.execute(c -> {
        var command = TUCommand.newBuilder(TUCommand.Type.REMOVE)
                .withTransportUnit(
                        TransportUnitMO.newBuilder().withPKey(TestData.TU_1_PKEY).build()
                )
                .build();
        publisher.publishEvent(command);
        assertThatThrownBy(() -> testee.findByPKey(TestData.TU_1_PKEY)).isInstanceOf(NotFoundException.class);
            c.setRollbackOnly();
            return c;
        });
    }
}