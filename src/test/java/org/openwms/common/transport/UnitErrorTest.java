/*
 * Copyright 2019 Heiko Scherrer
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A UnitErrorTest.
 *
 * @author Heiko Scherrer
 */
class UnitErrorTest {

    @Test void shall_create() {
        UnitError ue1 = UnitError.newBuilder().errorNo("4711").errorText("Error text").build();
        UnitError ue2 = UnitError.newBuilder().errorNo("4711").errorText("Error text2").build();
        UnitError ue3 = UnitError.newBuilder().errorNo("4712").errorText("Error text3").build();

        assertThat(ue1).isNotEqualTo(ue2);
        assertThat(ue2).isNotEqualTo(ue1);
        assertThat(ue1).isNotEqualTo(ue3);
        assertThat(ue3).isNotEqualTo(ue1);
        assertThat(ue1.toString()).isEqualTo(ue1.getErrorNo()+UnitError.SEPARATOR+ue1.getErrorText());
    }
}