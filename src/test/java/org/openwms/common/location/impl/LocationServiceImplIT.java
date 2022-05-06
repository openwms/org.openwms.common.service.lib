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
package org.openwms.common.location.impl;

import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestBase;
import org.openwms.common.TestData;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.common.location.LocationTypeService;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openwms.common.location.Location.DEF_CONSIDERED_IN_ALLOCATION;
import static org.openwms.common.location.Location.DEF_INCOMING_ACTIVE;
import static org.openwms.common.location.Location.DEF_LG_COUNTING_ACTIVE;
import static org.openwms.common.location.Location.DEF_OUTGOING_ACTIVE;
import static org.openwms.common.location.Location.DEF_PLC_STATE;

/**
 * A LocationServiceIT.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class LocationServiceImplIT extends TestBase {

    @Autowired
    private LocationTypeService locationTypeService;
    @Autowired
    private LocationService testee;
    @MockBean
    private AsyncTransactionApi transactionApi;
    @Autowired
    private EntityManager em;

    @Test void shall_throw_create_with_null() {
        assertThatThrownBy(() -> testee.create(null)).isInstanceOf(ServiceLayerException.class);
    }

    @Test void shall_throw_create_with_existing() {
        var location = Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000"));
        assertThatThrownBy(() -> testee.create(location)).hasMessageContaining("already exists");
    }

    @Test void shall_create_Location() {
        var location = Location.create(LocationPK.fromString("NEW_/NEW_/NEW_/NEW_/NEW_"));
        var result = testee.create(location);
        assertThat(result.getLocationId()).isEqualTo(LocationPK.fromString("NEW_/NEW_/NEW_/NEW_/NEW_"));
        assertThat(result.getNoMaxTransportUnits()).isEqualTo(Location.DEF_MAX_TU);
        assertThat(result.isLocationGroupCountingActive()).isEqualTo(DEF_LG_COUNTING_ACTIVE);
        assertThat(result.isInfeedActive()).isEqualTo(DEF_INCOMING_ACTIVE);
        assertThat(result.isOutfeedActive()).isEqualTo(DEF_OUTGOING_ACTIVE);
        assertThat(result.getPlcState()).isEqualTo(DEF_PLC_STATE);
        assertThat(result.isConsideredInAllocation()).isEqualTo(DEF_CONSIDERED_IN_ALLOCATION);
    }

    @Test void shall_create_Location_with_LocationType() {
        var locationType = locationTypeService.findByType("PG").orElseThrow();
        var location = Location.create(LocationPK.fromString("NEW_/NEW_/NEW_/NEW_/NEW_"));
        location.setLocationType(locationType);
        var result = testee.create(location);
        assertThat(result.getLocationId()).isEqualTo(LocationPK.fromString("NEW_/NEW_/NEW_/NEW_/NEW_"));
        assertThat(result.getNoMaxTransportUnits()).isEqualTo(Location.DEF_MAX_TU);
        assertThat(result.isLocationGroupCountingActive()).isEqualTo(DEF_LG_COUNTING_ACTIVE);
        assertThat(result.isInfeedActive()).isEqualTo(DEF_INCOMING_ACTIVE);
        assertThat(result.isOutfeedActive()).isEqualTo(DEF_OUTGOING_ACTIVE);
        assertThat(result.getPlcState()).isEqualTo(DEF_PLC_STATE);
        assertThat(result.isConsideredInAllocation()).isEqualTo(DEF_CONSIDERED_IN_ALLOCATION);
    }

    @Test void test_finder_with_null() {
        assertThatThrownBy(() -> testee.findByLocationPk(null)).hasMessageContaining("findByLocationPk.locationId");
    }

    @Test void test_finder_with_invalid() {
        assertThatThrownBy(() -> testee.findByLocationId("FOOBAR")).hasMessageContaining("is not valid");
    }

    @Test void test_finder_with_empty() {
        assertThatThrownBy(() -> testee.findByLocationId("")).hasMessageContaining("findByLocationId.locationId");
    }

    @Test void shall_find_existing_by_ID() {
        Optional<Location> byLocationId = testee.findByLocationPk(LocationPK.fromString(TestData.LOCATION_ID_EXT));

        assertThat(byLocationId).isNotEmpty();
        assertThat(byLocationId.get().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_EXT));
    }

    @Test void shall_throw_update_with_unknown() {
        var location = Location.create(LocationPK.fromString("UNKN/UNKN/UNKN/UNKN/UNKN"));
        assertThatThrownBy(() -> testee.save(location)).hasMessageContaining("does not exist");
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
        assertThrows(NotFoundException.class, () -> testee.save(newOne));
    }
}
