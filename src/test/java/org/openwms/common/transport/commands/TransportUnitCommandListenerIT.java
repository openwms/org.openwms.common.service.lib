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

import org.ameba.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitCommandListenerIT.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@CommonApplicationTest
@Transactional
@Rollback
class TransportUnitCommandListenerIT {

    @Autowired
    private TransportUnitCommandListener listener;
    @Autowired
    private TransportUnitService service;

    @Test void test_REMOVE_command() {
        assertThat(service.findByPKey(TestData.TU_1_PKEY)).isNotNull();
        listener.onCommand(TUCommand.newBuilder(TUCommand.Type.REMOVE)
                .withTransportUnit(
                        TransportUnitMO.newBuilder().withPKey(TestData.TU_1_PKEY).build()
                )
                .build()
        );
        assertThatThrownBy(() -> service.findByPKey(TestData.TU_1_PKEY)).isInstanceOf(NotFoundException.class);
    }

    @Test void test_CHANGE_ACTUAL_LOCATION_command() {
        assertThat(service.findByPKey(TestData.TU_1_PKEY).getActualLocation().getLocationId()).isNotEqualTo(TestData.LOCATION_ID_FGIN0001LEFT);
        listener.onCommand(TUCommand.newBuilder(TUCommand.Type.CHANGE_ACTUAL_LOCATION)
                .withTransportUnit(
                        TransportUnitMO.newBuilder()
                                .withPKey(TestData.TU_1_PKEY)
                                .withBarcode(TestData.TU_1_ID)
                                .withActualLocation(TestData.LOCATION_ID_FGIN0001LEFT)
                                .build()
                )
                .build()
        );
        assertThat(service.findByPKey(TestData.TU_1_PKEY).getActualLocation().getLocationId().toString()).isEqualTo(TestData.LOCATION_ID_FGIN0001LEFT);
    }
}