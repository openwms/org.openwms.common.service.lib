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
package org.openwms.common.transport;

import org.junit.jupiter.api.Test;
import org.openwms.common.location.LocationType;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TypePlacingRuleTest.
 *
 * @author Heiko Scherrer
 */
class TypePlacingRuleTest {

    @Test void testCreation() {
        new TypePlacingRule();
        TypePlacingRule rule1 = new TypePlacingRule(TransportUnitType.newBuilder("PG").build(), new LocationType("Pallet Conveyor"));
        assertThat(rule1.getPrivilegeLevel()).isEqualTo(TypePlacingRule.DEF_PRIVILEGE_LEVEL);
        assertThat(rule1.getTransportUnitType()).isEqualTo(TransportUnitType.newBuilder("PG").build());
        assertThat(rule1.getAllowedLocationType()).isEqualTo(new LocationType("Pallet Conveyor"));
        assertThat(rule1.toString()).hasToString(0+TypePlacingRule.SEPARATOR+"PG"+TypePlacingRule.SEPARATOR+"Pallet Conveyor");
    }

    @Test void testAssertedCreation() {
        LocationType locationType = new LocationType("Pallet Conveyor");
        TransportUnitType tut = TransportUnitType.newBuilder("PG").build();
        assertThatThrownBy(
                () -> new TypePlacingRule(null, locationType))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                () -> new TypePlacingRule(tut, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                () -> new TypePlacingRule(null, locationType, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                () -> new TypePlacingRule(tut, null, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testEquality() {
        TypePlacingRule rule1 = new TypePlacingRule(TransportUnitType.newBuilder("PG").build(), new LocationType("Pallet Conveyor"));
        TypePlacingRule rule11 = new TypePlacingRule(TransportUnitType.newBuilder("PG").build(), new LocationType("Pallet Conveyor"), TypePlacingRule.DEF_PRIVILEGE_LEVEL);
        TypePlacingRule rule2 = new TypePlacingRule(TransportUnitType.newBuilder("FG").build(), new LocationType("Flat Good Conveyor"));
        TypePlacingRule rule3 = new TypePlacingRule(TransportUnitType.newBuilder("FG").build(), new LocationType("Flat Good"));

        assertThat(rule1).isEqualTo(rule11);
        assertThat(rule11).isEqualTo(rule1);
        assertThat(rule1).isNotEqualTo(rule2);
        assertThat(rule2).isNotEqualTo(rule3);
        assertThat(rule3).isNotEqualTo(rule2);

        HashSet<TypePlacingRule> set = new HashSet<>();
        set.add(rule1);
        assertThat(set).hasSize(1);
        set.add(rule2);
        assertThat(set).hasSize(2);
        set.add(rule11);
        assertThat(set).hasSize(2);
    }
}