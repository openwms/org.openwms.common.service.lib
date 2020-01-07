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
package org.openwms.common.transport;

import org.junit.jupiter.api.Test;
import org.openwms.common.location.LocationType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitTypeTest.
 *
 * @author Heiko Scherrer
 */
class TransportUnitTypeTest {

    @Test void testCreationWithNull() {
        assertThatThrownBy(
                () -> new TransportUnitType(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testCreationWithEmpty() {
        assertThatThrownBy(
                () -> new TransportUnitType(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testDefaultValues() {
        TransportUnitType tut = new TransportUnitType("tut");
        assertThat(tut.getDescription()).isEqualTo(TransportUnitType.DEF_TYPE_DESCRIPTION);
        assertThat(tut.getLength()).isEqualTo(TransportUnitType.DEF_LENGTH);
        assertThat(tut.getHeight()).isEqualTo(TransportUnitType.DEF_HEIGHT);
        assertThat(tut.getWidth()).isEqualTo(TransportUnitType.DEF_WIDTH);
        assertThat(tut.getTransportUnits()).hasSize(0);
        assertThat(tut.getTypePlacingRules()).hasSize(0);
        assertThat(tut.getTypeStackingRules()).hasSize(0);
    }

    @Test void testPlacingRuleHandling() {
        LocationType locationType = new LocationType("conveyor");
        TransportUnitType tut = new TransportUnitType("tut");
        TypePlacingRule typePlacingRule = new TypePlacingRule(tut, locationType, 1);

        tut.addTypePlacingRule(typePlacingRule);
        assertThat(tut.getTypePlacingRules()).hasSize(1).contains(typePlacingRule);

        tut.removeTypePlacingRule(typePlacingRule);
        assertThat(tut.getTypePlacingRules()).hasSize(0);
    }
}
