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
package org.openwms.common.location;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A LocationTypeTest.
 *
 * @author Heiko Scherrer
 */
class LocationTypeTest {

    @Test
    void testCreationWithNull() {
        assertThatThrownBy(
                () -> new LocationType(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreationWithEmpty() {
        assertThatThrownBy(
                () -> new LocationType(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreation() {
        new LocationType(); //jpa
        LocationType type = new LocationType("PG");
        type.setDescription("pallet conveyor");
        type.setHeight(99);
        type.setLength(98);
        type.setWidth(97);
        assertThat(type.getDescription()).isEqualTo("pallet conveyor");
        assertThat(type.getHeight()).isEqualTo(99);
        assertThat(type.getLength()).isEqualTo(98);
        assertThat(type.getWidth()).isEqualTo(97);
    }

    @Test
    void testDefaultValues() {
        LocationType lt = new LocationType("conveyor");
        assertThat(lt.getDescription()).isEqualTo(LocationType.DEF_TYPE_DESCRIPTION);
        assertThat(lt.getHeight()).isEqualTo(LocationType.DEF_HEIGHT);
        assertThat(lt.getLength()).isEqualTo(LocationType.DEF_LENGTH);
        assertThat(lt.getWidth()).isEqualTo(LocationType.DEF_WIDTH);
        assertThat(lt.getType()).isEqualTo("conveyor");
    }

    @Test
    void testEqualityLight() {
        LocationType conveyor = new LocationType("conveyor");
        LocationType conveyor2 = new LocationType("conveyor");
        LocationType workplace = new LocationType("workplace");

        assertThat(workplace).isNotEqualTo(conveyor);
        assertThat(conveyor).isEqualTo(conveyor).isEqualTo(conveyor2);
        assertThat(conveyor2).isEqualTo(conveyor);

        HashSet<LocationType> set = new HashSet<>();
        set.add(conveyor);
        assertThat(set).hasSize(1);
        set.add(workplace);
        assertThat(set).hasSize(2);
        set.add(conveyor2);
        assertThat(set).hasSize(2);
    }

    @Test
    void testProperReturnOfToString() {
        LocationType conveyor = new LocationType("conveyor");
        assertThat(conveyor.toString()).hasToString("conveyor");
    }
}
