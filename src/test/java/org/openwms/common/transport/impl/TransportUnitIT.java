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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.UnitError;
import org.openwms.core.units.api.Weight;
import org.openwms.core.units.api.WeightUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitIT.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TransportUnitIT {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransportUnitRepository repository;

    private Location knownLocation;
    private TransportUnitType knownType;

    @BeforeEach void onBefore() {
        knownLocation = entityManager.find(Location.class, TestData.LOCATION_PK_EXT);
        knownType = entityManager.find(TransportUnitType.class, TestData.TUT_PK_PALLET);
    }

    @Test void shall_create_and_persist() {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getActualLocation()).isEqualTo(knownLocation);
        assertThat(transportUnit.getTransportUnitType()).isEqualTo(knownType);
    }

    @Test void shall_fail_with_transient_TUT() {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("NEVER_PERSISTED"), TransportUnitType.of("UNKNOWN_TUT"), knownLocation);
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("transient value");
    }


    @Test void shall_fail_with_transient_actualLocation() {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, Location.create(new LocationPK("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("transient value");
    }

    @Test void shall_fail_with_transient_targetLocation() {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit.setTargetLocation(Location.create(new LocationPK("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> entityManager.persistAndFlush(transportUnit))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unsaved transient");
    }

    @Test void shall_create_with_valid_targetLocation() {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, knownLocation);
        transportUnit.setTargetLocation(knownLocation);

        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.getTargetLocation()).isEqualTo(knownLocation);
    }


    @Test void shall_add_an_error_to_a_new_TU() {
        TransportUnit tu = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, knownLocation);
        UnitError saved = tu.addError(
                UnitError.newBuilder()
                        .errorNo("NEVER_PERSISTED")
                        .errorText("Damaged").build()
        );
        assertThat(saved.isNew()).isTrue();
        entityManager.persistAndFlush(tu);
        assertThat(entityManager.getEntityManager().createQuery("select count(u) from UnitError u").getResultList()).hasSize(1);
    }

    @Test void shall_add_an_error_to_a_managed_TU() {
        TransportUnit tu = new TransportUnit(Barcode.of("NEVER_PERSISTED"), knownType, knownLocation);
        tu = entityManager.persistAndFlush(tu);
        UnitError saved = tu.addError(
                UnitError.newBuilder()
                        .errorNo("NEVER_PERSISTED")
                        .errorText("Damaged").build()
        );
        assertThat(saved.isNew()).isFalse();
        assertThat(entityManager.getEntityManager().createQuery("select count(u) from UnitError u").getResultList()).hasSize(1);
    }

    @Test void shall_cascade_operations_to_children() {
        TransportUnit parent = new TransportUnit(Barcode.of("PARENT"), knownType, knownLocation);
        TransportUnit child = new TransportUnit(Barcode.of("CHILD"), knownType, knownLocation);

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
