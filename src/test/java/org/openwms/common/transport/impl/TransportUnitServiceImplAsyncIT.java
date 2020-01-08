/*
 * Copyright 2005-2020 the original author or authors.
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

import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * A TransportUnitServiceImplAsyncIT.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@CommonApplicationTest
@Transactional
class TransportUnitServiceImplAsyncIT {

    @Autowired
    private EntityManager em;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private TransportUnitService testee;

    @Test void shall_trigger_deletion() {
        TUCommand command = TUCommand.newBuilder(TUCommand.Type.REMOVE)
                .withTransportUnit(
                        TransportUnitMO.newBuilder().withPKey(TestData.TU_1_PKEY).build()
                )
                .build();
        publisher.publishEvent(command);
//        assertThatThrownBy(() -> testee.findByPKey(TestData.TU_1_PKEY)).isInstanceOf(NotFoundException.class);
    }
}