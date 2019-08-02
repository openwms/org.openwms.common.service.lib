/*
 * Copyright 2018 Heiko Scherrer
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.ObjectFactory;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.UnitError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitIT.
 *
 * @author Heiko Scherrer
 */
@ExtendWith(SpringExtension.class)// RunWith(SpringRunner.class)
@Tag("IntegrationTest")
class TransportUnitIT {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransportUnitRepository repository;
    @Autowired
    private TransportUnitTypeRepository typeRepository;

    private TransportUnitType knownType;
    private Location knownLocation1;

    @BeforeEach
    void onBefore() {
        repository.deleteAll();
        typeRepository.deleteAll();
        entityManager.flush();
        knownType = ObjectFactory.createTransportUnitType("Carton");
        knownLocation1 = Location.create(new LocationPK("KNO4", "KNO4", "KNO4", "KNO4", "KNO4"));
        entityManager.persist(knownType);
        entityManager.persist(knownLocation1);
        entityManager.flush();
    }

    @Test void testCreation() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("NEVER_PERSISTED");
        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(knownLocation1);
        repository.save(transportUnit);
    }

    @Test void testCreateTUWithUnknownType() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("NEVER_PERSISTED");
        TransportUnitType tut = ObjectFactory.createTransportUnitType("UNKNOWN_TUT");
        transportUnit.setTransportUnitType(tut);
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class);
    }


    @Test void testCreateTUWithUnknownActualLocation() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("NEVER_PERSISTED");
        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(Location.create(new LocationPK("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> repository.save(transportUnit))
                .isInstanceOf(DataAccessException.class);
    }

    @Test void testCreateTUWithUnknownTargetLocation() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("NEVER_PERSISTED");
        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(knownLocation1);
        transportUnit.setTargetLocation(Location.create(new LocationPK("UNKN", "UNKN", "UNKN", "UNKN", "UNKN")));
        assertThatThrownBy(
                () -> entityManager.persistAndFlush(transportUnit))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test void testSaveTUWithKnownActualLocation() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("4711");

        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(knownLocation1);

        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getActualLocation()).isNotNull();
    }

    @Test void testSaveTUwithKnownTargetLocation() {
        TransportUnit transportUnit = ObjectFactory.createTransportUnit("4711");

        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(knownLocation1);
        transportUnit.setTargetLocation(knownLocation1);

        transportUnit = repository.save(transportUnit);
        assertThat(transportUnit.isNew()).isFalse();
        assertThat(transportUnit.getActualLocation()).isNotNull();
    }

    @Test void testTUwithErrors() throws Exception {
        TransportUnit transportUnit = new TransportUnit(Barcode.of("4711"));

        transportUnit.setTransportUnitType(knownType);
        transportUnit.setActualLocation(knownLocation1);
        transportUnit.setTargetLocation(knownLocation1);

        transportUnit.addError(UnitError.newBuilder().build());
        Thread.sleep(100);
        transportUnit.addError(UnitError.newBuilder().build());
        entityManager.persist(transportUnit);

        Query query = entityManager.getEntityManager().createQuery("select count(ue) from UnitError ue", Long.class);

        Long cnt = (Long) query.getSingleResult();
        assertThat(cnt).isEqualTo(2);

        entityManager.remove(transportUnit);

        cnt = (Long) query.getSingleResult();
        assertThat(cnt).isEqualTo(0);
    }
}
