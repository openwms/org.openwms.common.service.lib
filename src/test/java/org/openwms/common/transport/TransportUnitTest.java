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
package org.openwms.common.transport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.core.units.api.Weight;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitTest.
 *
 * @author Heiko Scherrer
 */
class TransportUnitTest {

    @Nested
    @DisplayName("Creational")
    class CreationalTests {

        @Test void shall_fail_with_null_Barcode() {
            TransportUnitType tut = new TransportUnitType("KNOWN");
            Location actualLocation = Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000"));
            assertThatThrownBy(
                    () -> new TransportUnit(null, tut, actualLocation))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_fail_with_null_TUT() {
            Location actualLocation = Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000"));
            Barcode barcode = Barcode.of("4711");
            assertThatThrownBy(
                    () -> new TransportUnit(barcode, null, actualLocation))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_fail_with_null_Location() {
            Barcode barcode = Barcode.of("4711");
            TransportUnitType tut = new TransportUnitType("KNOWN");
            assertThatThrownBy(
                    () -> new TransportUnit(barcode, tut, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_create_with_defaults() {
            TransportUnit tu1 = new TransportUnit();// for JPA
            assertThat(tu1.getActualLocationDate()).isNull();

            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThat(tu.getBarcode()).isEqualTo(Barcode.of("4711"));
            assertThat(tu.getTransportUnitType()).isEqualTo(new TransportUnitType("KNOWN"));
            assertThat(tu.getActualLocation()).isEqualTo(Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThat(tu.getActualLocationDate()).isNotNull();
            assertThat(tu.getTargetLocation()).isNull();
            assertThat(tu.getWeight()).isEqualTo(Weight.ZERO);
            assertThat(tu.getState()).isEqualTo(TransportUnitState.AVAILABLE);
            assertThat(tu.getChildren()).isEmpty();
            assertThat(tu.getNoTransportUnits()).isZero();
            assertThat(tu.getInventoryDate()).isNotNull();
            assertThat(tu.getInventoryUser()).isEqualTo("init");
            assertThat(tu.getEmpty()).isNull();
            assertThat(tu.getErrors()).isEmpty();
        }

        @Test void testEqualityLight() {
            TransportUnit tu1 = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit tu2 = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0001/0001/0001/0001")));
            TransportUnit tu3 = new TransportUnit(Barcode.of("4712"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));

            assertThat(tu1).isEqualTo(tu2);
            assertThat(tu2).isEqualTo(tu1);

            assertThat(tu1).isNotEqualTo(tu3);
        }
    }

    @Nested
    @DisplayName("Lifecycle")
    class LifecycleTests {

        @Test void shall_fail_by_setting_null_for_TUT() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            tu.setTransportUnitType(new TransportUnitType("KNOWN2"));
            assertThat(tu.getTransportUnitType()).isEqualTo(new TransportUnitType("KNOWN2"));
            assertThatThrownBy(
                    () -> tu.setTransportUnitType(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_fail_by_setting_null_for_ActualLocation() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            tu.setActualLocation(Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0001")));
            assertThat(tu.getActualLocation()).isEqualTo(Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0001")));
            assertThatThrownBy(
                    () -> tu.setActualLocation(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_fail_by_setting_null_for_InventoryDate() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            tu.setInventoryDate(new Date());
            assertThat(tu.getInventoryDate()).isNotNull();
            assertThatThrownBy(
                    () -> tu.setInventoryDate(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_fail_with_add_null_as_error() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThatThrownBy(
                    () -> tu.addError(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void shall_add_an_error() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThat(tu.addError(UnitError.newBuilder().build())).isNotNull();
        }

        @Test void shall_fail_with_remove_null_as_child() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThatThrownBy(
                    () -> tu.addChild(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ChildrenHandling")
    class ChildrenHandling {

        @Test void shall_throw_with_null_remove() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThatThrownBy(
                    () -> tu.removeChild(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not be null")
            ;
        }

        @Test void shall_throw_with_null_add() {
            TransportUnit tu = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            assertThatThrownBy(
                    () -> tu.addChild(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not be null")
            ;
        }

        @Test void shall_add_and_remove_a_child_TU() {
            TransportUnit parent = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit child1 = new TransportUnit(Barcode.of("4712"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));

            parent.addChild(child1);
            assertThat(parent.getChildren()).hasSize(1).contains(child1);
            assertThat(child1.hasParent()).isTrue();
            assertThat(child1.getParent()).isEqualTo(parent);

            // Same parent check
            parent.addChild(child1);
            assertThat(parent.getChildren()).hasSize(1).contains(child1);
            assertThat(child1.hasParent()).isTrue();
            assertThat(child1.getParent()).isEqualTo(parent);

            parent.removeChild(child1);
            assertThat(parent.getChildren()).isEmpty();
            assertThat(child1.getParent()).isNull();
        }

        @Test void shall_fail_when_TU_is_not_a_child() {
            TransportUnit parent1 = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit parent2 = new TransportUnit(Barcode.of("4712"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit child1 = new TransportUnit(Barcode.of("4713"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit child2 = new TransportUnit(Barcode.of("4714"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));

            parent1.addChild(child1);

            assertThatThrownBy(
                    () -> parent1.removeChild(child2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not associated")
            ;

            parent2.addChild(child2);

            assertThatThrownBy(
                    () -> parent1.removeChild(child2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not associated")
            ;
            parent1.removeChild(child1);
        }

        @Test void shall_change_parent() {
            TransportUnit parent1 = new TransportUnit(Barcode.of("4711"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit parent2 = new TransportUnit(Barcode.of("4712"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit child1 = new TransportUnit(Barcode.of("4713"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));
            TransportUnit child2 = new TransportUnit(Barcode.of("4714"), new TransportUnitType("KNOWN"), Location.create(LocationPK.fromString("EXT_/0000/0000/0000/0000")));

            parent1.addChild(child1);
            parent2.addChild(child2);

            assertThat(child1.getParent()).isEqualTo(parent1);
            assertThat(child2.getParent()).isEqualTo(parent2);

            // change parent!
            parent1.addChild(child2);
            assertThat(parent1.getChildren()).hasSize(2);
            assertThat(parent2.getChildren()).isEmpty();
            assertThat(child2.getParent()).isEqualTo(parent1);
        }
    }
}