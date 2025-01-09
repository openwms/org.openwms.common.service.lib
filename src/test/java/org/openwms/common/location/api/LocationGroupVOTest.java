/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.location.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A LocationGroupVOTest.
 *
 * @author Heiko Scherrer
 */
class LocationGroupVOTest {

    @Test
    void testFlatteningATree() {
        LocationGroupVO l1 = new LocationGroupVO("L1");
        LocationGroupVO l2 = new LocationGroupVO("L2");
        LocationGroupVO l3 = new LocationGroupVO("L3");
        LocationGroupVO l4 = new LocationGroupVO("L4");
        LocationGroupVO l5 = new LocationGroupVO("L5");
        LocationGroupVO l6 = new LocationGroupVO("L6");
        l4.addChild(l5);
        l4.addChild(l6);
        l2.addChild(l3);
        l2.addChild(l4);
        l1.addChild(l2);

        List<String> all = l1.streamLocationGroups().map(LocationGroupVO::getName).toList();
        assertThat(all).contains("L1", "L2", "L3", "L4", "L5", "L6");
    }
}
