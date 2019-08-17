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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openwms.common.ApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitServiceImplTest.
 *
 * @author Heiko Scherrer
 */
@ApplicationTest
class TransportUnitServiceImplTest {

    @Autowired
    private TransportUnitService testee;

    @Nested
    @DisplayName("Creational Tests")
    class CreationalTests {
        @Test void create() {
            TransportUnit transportUnit = testee.create(Barcode.of("0815"), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false);
            assertThat(transportUnit).isNotNull();
        }

        @Test void create_with_null() {
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

        @Test void create_with_strict() {
            assertThatThrownBy(
                    () -> testee.create(Barcode.of(TestData.TU_1_ID), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, true))
                    .isInstanceOf(ServiceLayerException.class).hasMessageContaining("already exists");
        }

        @Test void create_without_strict() {
            TransportUnit transportUnit = testee.create(Barcode.of(TestData.TU_1_ID), TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, null);
            assertThat(transportUnit).isNotNull();
            assertThat(transportUnit.isNew()).isFalse();
            assertThat(transportUnit.getBarcode()).isEqualTo(Barcode.of(TestData.TU_1_ID));
        }
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

    @Nested
    @DisplayName("Finder Tests")
    class FinderTests {
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
    }

    @Test
    void findByBarcodes() {
    }

    @Test
    void findOnLocation() {
    }

    @Test
    void changeTarget() {
    }
}