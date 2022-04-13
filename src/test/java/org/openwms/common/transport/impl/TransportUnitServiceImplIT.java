/*
 * Copyright 2005-2022 the original author or authors.
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
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    @MockBean
    private AsyncTransactionApi transactionApi;

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
        var transportUnit = testee.create("0815", TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false);
        assertThat(transportUnit).isNotNull();
    }

    @Test
    void create_primitive_with_null() {
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create(null, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, false))
                .withMessageContaining("transportUnitBK");
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create("0815", null, TestData.LOCATION_ID_EXT, false))
                .withMessageContaining("transportUnitType");
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create("0815", TestData.TUT_TYPE_PALLET, null, false))
                .withMessageContaining("actualLocation");
    }

    @Test
    void create_primitive_with_strict() {
        Barcode barcode = generator.convert(TestData.TU_1_ID);
        assertThatExceptionOfType(ResourceExistsException.class)
                .isThrownBy(() -> testee.create(TestData.TU_1_ID, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, true))
                .withMessageContaining("already exists");
    }

    @Test
    void create_primitive_without_strict() {
        var transportUnit = testee.create(TestData.TU_1_ID, TestData.TUT_TYPE_PALLET, TestData.LOCATION_ID_EXT, null);
        assertThat(transportUnit).isNotNull();
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getBarcode()).isEqualTo(generator.convert(TestData.TU_1_ID));
    }

    @Test
    void create() {
        var transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        var transportUnit = testee.create("0815", transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), false);
        assertThat(transportUnit).isNotNull();
    }

    @Test
    void create_with_null() {
        var transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        var location = LocationPK.fromString(TestData.LOCATION_ID_EXT);
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create(null, transportUnitType, location, false))
                .withMessageContaining("transportUnitBK");
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create("0815", null, location, false))
                .withMessageContaining("transportUnitType");
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.create("0815", transportUnitType, null, false))
                .withMessageContaining("actualLocation");
    }

    @Test
    void create_with_strict() {
        var transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        var location = LocationPK.fromString(TestData.LOCATION_ID_EXT);
        assertThatExceptionOfType(ResourceExistsException.class)
                .isThrownBy(() -> testee.create(TestData.TU_1_ID, transportUnitType, location, true))
                .withMessageContaining("already exists");
    }

    @Test
    void create_without_strict() {
        var transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        var transportUnit = testee.create(TestData.TU_1_ID, transportUnitType, LocationPK.fromString(TestData.LOCATION_ID_EXT), null);
        assertThat(transportUnit).isNotNull();
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getBarcode()).isEqualTo(generator.convert(TestData.TU_1_ID));
    }

    @Test
    void shall_delete_multiple() {
        var transportUnitType = em.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
        testee.deleteTransportUnits(
                List.of(
                        new TransportUnit(generator.convert(TestData.TU_1_ID), transportUnitType, Location.create(LocationPK.fromString(TestData.LOCATION_ID_EXT)))
                )
        );
    }

    @Test
    void shall_change_target() {
        var transportUnit = testee.changeTarget(Barcode.of(TestData.TU_2_ID), TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(transportUnit.getTargetLocation().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_FGIN0001LEFT));
    }

    @Test
    void shall_change_target_invalid_Location() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.changeTarget(Barcode.of(TestData.TU_2_ID), "UNKW/UNKW/UNKW/UNKW/UNKW"))
                .withMessageContaining("Location with name");
    }

    @Test
    void shall_change_target_invalid_Barcode() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.changeTarget(Barcode.of("UNKNOWN"), TestData.LOCATION_ID_FGIN0001LEFT))
                .withMessageContaining("TransportUnit with Barcode [UNKNOWN] does not exist");
    }

    @Test
    void findByBarcode() {
        var tu = testee.findByBarcode(TestData.TU_1_ID);
        assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByBarcode_null() {
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.findByBarcode(null))
                .withMessageContaining("findByBarcode.transportUnitBK: must not be blank");
    }

    @Test
    void findByBarcode_404() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.findByBarcode("NOTEXISTS"))
                .withMessageContaining("TransportUnit with Barcode [NOTEXISTS] does not exist");
    }

    @Test
    void findByBarcodes() {
        var tu = testee.findByBarcodes(Collections.singletonList(generator.convert(TestData.TU_1_ID)));
        assertThat(tu).isNotNull();
        assertThat(tu.get(0)).hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByBarcodes_null() {
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.findByBarcodes(null))
                .withMessageContaining("findByBarcodes.barcodes: must not be empty");
    }

    @Test
    void findByBarcodes_404() {
        var tu = testee.findByBarcodes(asList(generator.convert("NOTEXISTS")));
        assertThat(tu).isEmpty();
    }

    @Test
    void findByPKey() {
        var tu = testee.findByPKey(TestData.TU_1_PKEY);
        assertThat(tu).isNotNull().hasFieldOrPropertyWithValue("barcode", generator.convert(TestData.TU_1_ID));
    }

    @Test
    void findByPKey_null() {
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.findByPKey(null))
                .withMessageContaining("blank");
    }

    @Test
    void findByPKey_404() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.findByPKey("UNKNOWN"))
                .withMessageContaining("pKey");
    }

    @Test
    void findOnLocation_null() {
        assertThatExceptionOfType(ServiceLayerException.class)
                .isThrownBy(() -> testee.findOnLocation(null))
                .withMessageContaining("actualLocation");
    }

    @Test
    void findOnLocation_404() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.findOnLocation("EXT_/9999/9999/9999/9999"))
                .withMessageContaining("not exist");
    }

    @Test
    void findOnLocation_Empty() {
        var tus = testee.findOnLocation(TestData.LOCATION_ID_FGIN0001LEFT);
        assertThat(tus.isEmpty()).isTrue();
    }

    @Test
    void findOnLocation() {
        var tus = testee.findOnLocation(TestData.LOCATION_ID_EXT);
        assertThat(tus.isEmpty()).isFalse();
    }
}