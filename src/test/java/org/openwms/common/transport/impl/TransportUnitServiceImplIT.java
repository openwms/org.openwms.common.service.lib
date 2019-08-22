/*
 * Copyright 2019 Heiko Scherrer
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
import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Test;
import org.openwms.common.ApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitServiceImplIT.
 *
 * @author Heiko Scherrer
 */
@ApplicationTest
class TransportUnitServiceImplIT {

    @Autowired
    private EntityManager em;
    @Autowired
    private TransportUnitService testee;

        @Test void create_primitive() {
            TransportUnit transportUnit = testee.create(Barcode.of("0815"), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false);
            assertThat(transportUnit).isNotNull();
        }

        @Test void create_primitive_with_null() {
            assertThatThrownBy(
                    () -> testee.create(null, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("barcode");
            assertThatThrownBy(
                    () -> testee.create(Barcode.of("0815"), null, TestData.LOCATION_ID_EXT, false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitType");
            assertThatThrownBy(
                    () -> testee.create(Barcode.of("0815"), TestData.TUT_TYPE_PALLET, null, false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("actualLocation");
        }

        @Test void create_primitive_with_strict() {
            assertThatThrownBy(
                    () -> testee.create(Barcode.of(TestData.TU_1_ID), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, true))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("already exists");
        }

        @Test void create_primitive_without_strict() {
            TransportUnit transportUnit = testee.create(Barcode.of(TestData.TU_1_ID), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, null);
            assertThat(transportUnit).isNotNull();
            assertThat(transportUnit.isNew()).isFalse();
            assertThat(transportUnit.getBarcode()).isEqualTo(Barcode.of(TestData.TU_1_ID));
        }

        @Test void create() {
            TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
            TransportUnit transportUnit = testee.create(Barcode.of("0815"), transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), false);
            assertThat(transportUnit).isNotNull();
        }

        @Test void create_with_null() {
            TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
            assertThatThrownBy(
                    () -> testee.create(null, transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("barcode");
            assertThatThrownBy(
                    () -> testee.create(Barcode.of("0815"), null, LocationPK.fromString(TestData.LOCATION_ID_EXT), false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitType");
            assertThatThrownBy(
                    () -> testee.create(Barcode.of("0815"), transportUnitType, null, false))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("actualLocation");
        }

        @Test void create_with_strict() {
            TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
            assertThatThrownBy(
                    () -> testee.create(Barcode.of(TestData.TU_1_ID), transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), true))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("already exists");
        }

        @Test void create_without_strict() {
            TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
            TransportUnit transportUnit = testee.create(Barcode.of(TestData.TU_1_ID), transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), null);
            assertThat(transportUnit).isNotNull();
            assertThat(transportUnit.isNew()).isFalse();
            assertThat(transportUnit.getBarcode()).isEqualTo(Barcode.of(TestData.TU_1_ID));
        }


    @Test
    void update() {
    }

    @Test
    void moveTransportUnit() {
    }

    @Test
    void deleteTransportUnits() {
    }

    @Test
    void onEvent() {
    }

        @Test void findByBarcode() {
            TransportUnit tu = testee.findByBarcode(Barcode.of(TestData.TU_1_ID));
            assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", Barcode.of(TestData.TU_1_ID));
        }

        @Test void findByBarcode_null() {
            assertThatThrownBy(
                    () -> testee.findByBarcode(null))
                    .isInstanceOf(NotFoundException.class).hasMessageContaining("null");
        }

        @Test void findByBarcode_404() {
            assertThatThrownBy(
                    () -> testee.findByBarcode(Barcode.of("NOTEXISTS")))
                    .isInstanceOf(NotFoundException.class).hasMessageContaining("not found");
        }

        @Test void findByBarcodes() {
            List<TransportUnit> tu = testee.findByBarcodes(asList(Barcode.of(TestData.TU_1_ID)));
            assertThat(tu).isNotNull();
            assertThat(tu.get(0)).hasFieldOrPropertyWithValue("barcode", Barcode.of(TestData.TU_1_ID));
        }

        @Test void findByBarcodes_null() {
            List<TransportUnit> tu = testee.findByBarcodes(null);
            assertThat(tu).isEmpty();
        }

        @Test void findByBarcodes_404() {
            List<TransportUnit> tu = testee.findByBarcodes(asList(Barcode.of("NOTEXISTS")));
            assertThat(tu).isEmpty();
        }

        @Test void findByPKey() {
            TransportUnit tu = testee.findByPKey(TestData.TU_1_PKEY);
            assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", Barcode.of(TestData.TU_1_ID));
        }

        @Test void findByPKey_null() {
            assertThatThrownBy(
                    () -> testee.findByPKey(null))
                    .isInstanceOf(NotFoundException.class).hasMessageContaining("null");
        }

        @Test void findByPKey_404() {
            assertThatThrownBy(
                    () -> testee.findByPKey("UNKNOWN"))
                    .isInstanceOf(NotFoundException.class).hasMessageContaining("pKey");
        }

        @Test void findOnLocation_null() {
            assertThatThrownBy(
                    () -> testee.findOnLocation(null))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("null");
        }

        @Test void findOnLocation_404() {
            assertThatThrownBy(
                    () -> testee.findOnLocation("EXT_/9999/9999/9999/9999"))
                    .isInstanceOf(NotFoundException.class).hasMessageContaining("not found");
        }

        @Test void findOnLocation_Empty() {
            List<TransportUnit> tus = testee.findOnLocation(TestData.LOCATION_ID_FGIN0001LEFT);
            assertThat(tus.isEmpty()).isTrue();
        }

        @Test void findOnLocation() {
            List<TransportUnit> tus = testee.findOnLocation(TestData.LOCATION_ID_EXT);
            assertThat(tus.isEmpty()).isFalse();
        }

    @Test
    void changeTarget() {
    }
}