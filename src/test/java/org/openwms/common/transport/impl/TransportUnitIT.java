/*
 * Copyright 2005-2023 the original author or authors.
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonDataTest;
import org.openwms.common.TestData;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.UnitError;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.openwms.core.units.api.Weight;
import org.openwms.core.units.api.WeightUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * A TransportUnitIT.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
class TransportUnitIT {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransportUnitRepository repository;
    @MockBean
    private BarcodeGenerator generator;

    private Location knownLocation;
    private TransportUnitType knownType;

    @BeforeEach void onSetup() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "%s");
        System.setProperty("owms.common.barcode.padder", "0");
        knownLocation = entityManager.find(Location.class, TestData.LOCATION_PK_EXT);
        knownType = entityManager.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
    }

    @AfterEach void onTeardown() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "");
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "");
    }

    @Test void shall_create_and_persist() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var transportUnit = new TransportUnit(generator.convert("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getActualLocation()).isEqualTo(knownLocation);
        assertThat(transportUnit.getTransportUnitType()).isEqualTo(knownType);
    }

    @Test void shall_fail_with_transient_TUT() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var transportUnit = new TransportUnit(generator.convert("NEVER_PERSISTED"), TransportUnitType.of("UNKNOWN_TUT"), knownLocation);
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("transient value");
    }


    @Test void shall_fail_with_transient_actualLocation() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var transportUnit = new TransportUnit(generator.convert("NEVER_PERSISTED"), knownType, Location.create(LocationPK.of("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("transient value");
    }

    @Test void shall_fail_with_transient_targetLocation() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var transportUnit = new TransportUnit(generator.convert("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit.setTargetLocation(Location.create(LocationPK.of("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> entityManager.persistAndFlush(transportUnit))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unsaved transient");
    }

    @Test void shall_create_with_valid_targetLocation() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var transportUnit = new TransportUnit(generator.convert("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit.setTargetLocation(knownLocation);

        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.getTargetLocation()).isEqualTo(knownLocation);
    }


    @Test void shall_cascade_errors_of_TU() {
        when(generator.convert("NEVER_PERSISTED")).thenReturn(Barcode.of("NEVER_PERSISTED"));
        var tu = new TransportUnit(generator.convert("NEVER_PERSISTED"), knownType, knownLocation);
        var saved = tu.addError(
                UnitError.newBuilder()
                        .errorNo("NEVER_PERSISTED")
                        .errorText("Damaged").build()
        );
        assertThat(tu.getErrors()).contains(saved);
        assertThat(saved.getTransportUnit()).isEqualTo(tu);
        entityManager.persistAndFlush(tu);
        assertThat(entityManager.getEntityManager().createQuery("select count(u) from UnitError u").getResultList()).hasSize(1);
    }

    @Test void shall_cascade_operations_to_children() {
        when(generator.convert("PARENT")).thenReturn(Barcode.of("PARENT"));
        var parent = new TransportUnit(generator.convert("PARENT"), knownType, knownLocation);
        when(generator.convert("CHILD")).thenReturn(Barcode.of("CHILD"));
        var child = new TransportUnit(generator.convert("CHILD"), knownType, knownLocation);

        parent.addChild(child);

        parent = entityManager.persistAndFlush(parent);

        assertThat(parent.isNew()).isFalse();
        assertThat(child.isNew()).isFalse();
        assertThat(parent.getChildren()).hasSize(1);

        parent.getChildren().iterator().next().setWeight(Weight.of(1, WeightUnit.KG));
        entityManager.flush();
        assertThat(child.getWeight()).isEqualTo(Weight.of(1, WeightUnit.KG));
    }
}
