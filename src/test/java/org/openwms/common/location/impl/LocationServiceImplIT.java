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
package org.openwms.common.location.impl;

import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestBase;
import org.openwms.common.TestData;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.transactions.api.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

/**
 * A LocationServiceIT.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class LocationServiceImplIT extends TestBase {

    @Autowired
    private LocationService testee;
    @MockBean
    private AsyncTransactionApi transactionApi;
    @Autowired
    private EntityManager em;

    @Test void test_finder_with_null() {
        assertThatThrownBy(
                () -> testee.findByLocationPk(null)).hasMessageContaining("findByLocationPk.locationId");
    }

    @Test void test_finder_with_empty() {
        assertThatThrownBy(
                () -> testee.findByLocationId("")).hasMessageContaining("findByLocationId.locationId");
    }

    @Test void shall_find_existing_by_ID() {
        Optional<Location> byLocationId = testee.findByLocationPk(LocationPK.fromString(TestData.LOCATION_ID_EXT));

        assertThat(byLocationId).isNotEmpty();
        assertThat(byLocationId.get().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_EXT));
    }

    @Test void shall_modify_existing_one() {
        // arrange
        Location existing = em.find(Location.class, TestData.LOCATION_PK_EXT);
        existing.setPlcState(111);
        existing = em.merge(existing);

        // act
        Location updated = testee.save(existing);

        // assert
        assertThat(updated.getPlcState()).isEqualTo(111);
    }

    @Test void shall_fail_modifying_new_one() {
        // arrange
        Location newOne = Location.create(LocationPK.fromString("UNKOWN/UNKOWN/UNKOWN/UNKOWN/UNKOWN"));

        // act & assert
        assertThrows(ServiceLayerException.class, () -> testee.save(newOne));
    }
}
