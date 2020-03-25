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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.api.commands.MessageCommand;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.api.messages.TransportUnitTypeMO;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A TransportUnitCommandListenerIT.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
@Transactional
@Rollback
class TransportUnitCommandListenerIT {

    @Autowired
    private TransportUnitCommandListener listener;
    @Autowired
    private TransportUnitService service;

    @BeforeAll
    public static void enableWithRabbitOnly() {
        assumeTrue(System.getProperty("spring.profiles.active", "default").contains(SpringProfiles.ASYNCHRONOUS_PROFILE));
    }

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

    @Test void test_CHANGE_TARGET_command() {
        assertThat(service.findByPKey(TestData.TU_1_PKEY).getActualLocation().getLocationId()).isNotEqualTo(TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(service.findByPKey(TestData.TU_1_PKEY).getTargetLocation()).isNull();
        listener.onCommand(TUCommand.newBuilder(TUCommand.Type.CHANGE_TARGET)
                .withTransportUnit(
                        TransportUnitMO.newBuilder()
                                .withBarcode(TestData.TU_1_ID)
                                .withTargetLocation(TestData.LOCATION_ID_FGIN0001LEFT)
                                .build()
                )
                .build()
        );
        assertThat(service.findByPKey(TestData.TU_1_PKEY).getTargetLocation().getLocationId().toString()).isEqualTo(TestData.LOCATION_ID_FGIN0001LEFT);
    }

    @Test void test_CREATE_command() {
        listener.onCommand(TUCommand.newBuilder(TUCommand.Type.CREATE)
                .withTransportUnit(
                        TransportUnitMO.newBuilder()
                                .withBarcode("0815")
                                .withTransportUnitType(
                                        TransportUnitTypeMO.newBuilder()
                                                .type(TestData.TUT_TYPE_PALLET)
                                                .build()
                                )
                                .withActualLocation(TestData.LOCATION_ID_FGIN0001LEFT)
                                .build()
                )
                .build()
        );
        TransportUnit tu = service.findByBarcode(Barcode.of("0815"));
        assertThat(tu.getActualLocation().getLocationId().toString()).isEqualTo(TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(tu.getTransportUnitType().getType()).isEqualTo(TestData.TUT_TYPE_PALLET);
    }

    @Test void test_ADD_TO_TU_command() {
        listener.onCommand(
                MessageCommand.newBuilder(MessageCommand.Type.ADD_TO_TU)
                        .withTransportUnitId(TestData.TU_1_ID)
                        .withMessageNumber("999")
                        .withMessageText("TEXT")
                        .withMessageOccurred(new Date()).build()
        );
        TransportUnit tu = service.findByBarcode(Barcode.of(TestData.TU_1_ID));
        assertThat(tu.getErrors()).hasSize(1);
        assertThat(tu.getErrors().get(0).getErrorNo()).isEqualTo("999");
        assertThat(tu.getErrors().get(0).getErrorText()).isEqualTo("TEXT");
        assertThat(tu.getErrors().get(0).getCreateDt()).isNotNull();
    }
}