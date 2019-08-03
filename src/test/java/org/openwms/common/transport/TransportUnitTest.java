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
package org.openwms.common.transport;

import org.junit.jupiter.api.Test;
import org.openwms.common.units.Weight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitTest.
 *
 * @author Heiko Scherrer
 */
class TransportUnitTest {

    @Test void testCreationWithEmptyString() {
        assertThatThrownBy(
                () -> ObjectFactory.createTransportUnit(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testDefaultValues() {
        TransportUnit tu = ObjectFactory.createTransportUnit("4711");
        assertThat(tu.getBarcode()).isEqualTo(Barcode.of("4711"));
        assertThat(tu.getWeight()).isEqualTo(new Weight("0"));
        assertThat(tu.getState()).isEqualTo(TransportUnitState.AVAILABLE);
        assertThat(tu.getErrors()).hasSize(0);
        assertThat(tu.getChildren()).hasSize(0);
    }

    @Test void testAddErrorWithNull() {
        TransportUnit tu = ObjectFactory.createTransportUnit("4711");
        assertThatThrownBy(
                () -> tu.addError(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testUnitErrorHandling() {
        TransportUnit tu = ObjectFactory.createTransportUnit("4711");
        assertThat(tu.getErrors()).hasSize(0);
        tu.addError(UnitError.newBuilder().errorNo("4711").errorText("Damaged").build());
        assertThat(tu.getErrors()).hasSize(1).containsValues(UnitError.newBuilder().errorNo("4711").errorText("Damaged").build());
    }

    @Test void testAddChildWithNull() {
        TransportUnit tu = ObjectFactory.createTransportUnit("4711");
        assertThatThrownBy(
                () -> tu.addChild(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testRemoveChildWithNull() {
        TransportUnit tu = ObjectFactory.createTransportUnit("4711");
        tu.removeChild(null);
        assertThatThrownBy(
                () -> tu.removeChild(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testChildrenHandling() {
        TransportUnit tu1 = ObjectFactory.createTransportUnit("4711");
        TransportUnit tu2 = ObjectFactory.createTransportUnit("4712");
        TransportUnit tu3 = ObjectFactory.createTransportUnit("4713");

        tu1.addChild(tu2);
        assertThat(tu1.getChildren()).hasSize(1).contains(tu2);
        assertThat(tu2.getParent()).isEqualTo(tu1);

        tu1.addChild(tu3);
        assertThat(tu1.getChildren()).hasSize(2).contains(tu3);

        tu2.addChild(tu3);
        assertThat(tu1.getChildren()).hasSize(1);
        assertThat(tu2.getChildren()).hasSize(1).contains(tu3);
        assertThat(tu3.getParent()).isEqualTo(tu2);

        tu2.removeChild(tu3);
        assertThat(tu2.getChildren()).hasSize(0);
        assertThat(tu3.getParent()).isNull();

        assertThatThrownBy(
                () -> tu1.removeChild(tu3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testEqualityLight() {
        TransportUnit tu1 = ObjectFactory.createTransportUnit("4711");
        TransportUnit tu2 = ObjectFactory.createTransportUnit("4711");
        TransportUnit tu3 = ObjectFactory.createTransportUnit("4712");

        assertThat(tu1).isEqualTo(tu2);
        assertThat(tu2).isEqualTo(tu1);

        assertThat(tu1).isNotEqualTo(tu3);
    }
}