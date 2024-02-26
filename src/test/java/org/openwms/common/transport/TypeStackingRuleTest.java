/*
 * Copyright 2005-2024 the original author or authors.
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

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TypeStackingRuleTest.
 *
 * @author Heiko Scherrer
 */
class TypeStackingRuleTest {

    @Test
    void testCreation() {
        new TypeStackingRule(); // For JPA
        TypeStackingRule rule = new TypeStackingRule(1, TransportUnitType.newBuilder("PG").build(), TransportUnitType.newBuilder("FG").build());
        TransportUnitType tut = TransportUnitType.newBuilder("PG").build();
        assertThat(rule.getNoTransportUnits()).isEqualTo(1);
        assertThat(rule.getBaseTransportUnitType()).isEqualTo(TransportUnitType.newBuilder("PG").build());
        assertThat(rule.getAllowedTransportUnitType()).isEqualTo(TransportUnitType.newBuilder("FG").build());
        assertThat(rule.toString()).hasToString(1+TypeStackingRule.SEPARATOR+"PG"+TypeStackingRule.SEPARATOR+"FG");
        assertThatThrownBy(
                () -> new TypeStackingRule(1, null, tut))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                () -> new TypeStackingRule(1, tut, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testEquality() {
        TypeStackingRule rule1 = new TypeStackingRule(1, TransportUnitType.newBuilder("PG").build(), TransportUnitType.newBuilder("FG").build());
        TypeStackingRule rule11 = new TypeStackingRule(1, TransportUnitType.newBuilder("PG").build(), TransportUnitType.newBuilder("FG").build());
        TypeStackingRule rule2 = new TypeStackingRule(2, TransportUnitType.newBuilder("PG").build(), TransportUnitType.newBuilder("FG").build());
        TypeStackingRule rule3 = new TypeStackingRule(2, TransportUnitType.newBuilder("FG").build(), TransportUnitType.newBuilder("FG").build());
        TypeStackingRule rule4 = new TypeStackingRule(2, TransportUnitType.newBuilder("FG").build(), TransportUnitType.newBuilder("PG").build());

        assertThat(rule1).isEqualTo(rule11).isNotEqualTo(rule2);
        assertThat(rule11).isEqualTo(rule1);
        assertThat(rule2).isNotEqualTo(rule3);
        assertThat(rule3).isNotEqualTo(rule2).isNotEqualTo(rule4);
        assertThat(rule4).isNotEqualTo(rule3);

        HashSet<TypeStackingRule> set = new HashSet<>();
        set.add(rule1);
        assertThat(set).hasSize(1);
        set.add(rule2);
        assertThat(set).hasSize(2);
        set.add(rule11);
        assertThat(set).hasSize(2);
    }
}