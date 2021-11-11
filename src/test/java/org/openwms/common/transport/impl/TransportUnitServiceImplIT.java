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
package org.openwms.common.transport.impl;

import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitServiceImplIT.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TransportUnitServiceImplIT {

    @Autowired
    private EntityManager em;
    @Autowired
    private TransportUnitService testee;
    @Autowired
    private BarcodeGenerator generator;

    @BeforeEach
    void onSetup() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "%s");
        System.setProperty("owms.common.barcode.padder", "0");
    }

    @AfterEach
    void onTeardown() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "");
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "");
    }

    @Test
    void create_primitive() {
        TransportUnit transportUnit = testee.create("0815", TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false);
        assertThat(transportUnit).isNotNull();
    }

    @Test
    void create_primitive_with_null() {
        assertThatThrownBy(
                () -> testee.create(null, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitBK");
        assertThatThrownBy(
                () -> testee.create("0815", null, TestData.LOCATION_ID_EXT, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitType");
        assertThatThrownBy(
                () -> testee.create("0815", TestData.TUT_TYPE_PALLET, null, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("actualLocation");
    }

    @Test
    void create_primitive_with_strict() {
        Barcode barcode = generator.convert(TestData.TU_1_ID);
        assertThatThrownBy(
                () -> {
                    testee.create(TestData.TU_1_ID, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, true);
                })
                .isInstanceOf(ResourceExistsException.class).hasMessageContaining("already exists");
    }

    @Test
    void create_primitive_without_strict() {
        TransportUnit transportUnit = testee.create(TestData.TU_1_ID, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, null);
        assertThat(transportUnit).isNotNull();
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getBarcode()).isEqualTo(generator.convert(TestData.TU_1_ID));
    }

    @Test
    void create() {
        TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        TransportUnit transportUnit = testee.create("0815", transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), false);
        assertThat(transportUnit).isNotNull();
    }

    @Test
    void create_with_null() {
        TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        LocationPK location = LocationPK.fromString(TestData.LOCATION_ID_EXT);
        assertThatThrownBy(
                () -> testee.create(null, transportUnitType, location, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitBK");
        assertThatThrownBy(
                () -> testee.create("0815", null, location, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitType");
        assertThatThrownBy(
                () -> testee.create("0815", transportUnitType, null, false))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("actualLocation");
    }

    @Test
    void create_with_strict() {
        TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        LocationPK location = LocationPK.fromString(TestData.LOCATION_ID_EXT);
        assertThatThrownBy(
                () -> testee.create(TestData.TU_1_ID, transportUnitType, location, true))
                .isInstanceOf(ResourceExistsException.class).hasMessageContaining("already exists");
    }

    @Test
    void create_without_strict() {
        TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        TransportUnit transportUnit = testee.create(TestData.TU_1_ID, transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), null);
        assertThat(transportUnit).isNotNull();
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getBarcode()).isEqualTo(generator.convert(TestData.TU_1_ID));
    }

    @Test
    void shall_delete_multiple() {
        TransportUnitType transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        testee.deleteTransportUnits(
                List.of(
                        new TransportUnit(generator.convert(TestData.TU_1_ID), transportUnitType, Location.create(LocationPK.fromString(TestData.LOCATION_ID_EXT)))
                )
        );
    }

    @Test
    void shall_change_target() {
        TransportUnit transportUnit = testee.changeTarget(Barcode.of(TestData.TU_2_ID), TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(transportUnit.getTargetLocation().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_FGIN0001LEFT));
    }

    @Test
    void shall_change_target_invalid_Location() {
        assertThatThrownBy
                (() -> testee.changeTarget(Barcode.of(TestData.TU_2_ID), "UNKW/UNKW/UNKW/UNKW/UNKW"))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("Location with locationId");
    }

    @Test
    void shall_change_target_invalid_Barcode() {
        assertThatThrownBy
                (() -> testee.changeTarget(Barcode.of("UNKNOWN"), TestData.LOCATION_ID_FGIN0001LEFT))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("TransportUnit with Barcode [UNKNOWN] does not exist");
    }

    @Test
    void findByBarcode() {
        TransportUnit tu = testee.findByBarcode(TestData.TU_1_ID);
        assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByBarcode_null() {
        assertThatThrownBy(
                () -> testee.findByBarcode(null))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("transportUnitBK: must not be empty");
    }

    @Test
    void findByBarcode_404() {
        assertThatThrownBy(
                () -> testee.findByBarcode("NOTEXISTS"))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("TransportUnit with Barcode [NOTEXISTS] does not exist");
    }

    @Test
    void findByBarcodes() {
        List<TransportUnit> tu = testee.findByBarcodes(Collections.singletonList(generator.convert(TestData.TU_1_ID)));
        assertThat(tu).isNotNull();
        assertThat(tu.get(0)).hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByBarcodes_null() {
        List<TransportUnit> tu = testee.findByBarcodes(null);
        assertThat(tu).isEmpty();
    }

    @Test
    void findByBarcodes_404() {
        List<TransportUnit> tu = testee.findByBarcodes(asList(generator.convert("NOTEXISTS")));
        assertThat(tu).isEmpty();
    }

    @Test
    void findByPKey() {
        TransportUnit tu = testee.findByPKey(TestData.TU_1_PKEY);
        assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByPKey_null() {
        assertThatThrownBy(
                () -> testee.findByPKey(null))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("empty");
    }

    @Test
    void findByPKey_404() {
        assertThatThrownBy(
                () -> testee.findByPKey("UNKNOWN"))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("pKey");
    }

    @Test
    void findOnLocation_null() {
        assertThatThrownBy(
                () -> testee.findOnLocation(null))
                .isInstanceOf(ServiceLayerException.class).hasMessageContaining("actualLocation");
    }

    @Test
    void findOnLocation_404() {
        assertThatThrownBy(
                () -> testee.findOnLocation("EXT_/9999/9999/9999/9999"))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("not found");
    }

    @Test
    void findOnLocation_Empty() {
        List<TransportUnit> tus = testee.findOnLocation(TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(tus.isEmpty()).isTrue();
    }

    @Test
    void findOnLocation() {
        List<TransportUnit> tus = testee.findOnLocation(TestData.LOCATION_ID_EXT);
        assertThat(tus.isEmpty()).isFalse();
    }
}