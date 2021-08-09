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
package org.openwms.common.transport.barcode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A BarcodeTest.
 *
 * @author Heiko Scherrer
 */
class BarcodeTest {

    @BeforeEach
    void onSetup() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.padder", "0");
        System.setProperty("owms.common.barcode.length", "20");
    }

    @AfterEach
    void onTeardown() {
        System.setProperty("owms.common.barcode.padder", "");
        System.setProperty("owms.common.barcode.length", "");
    }

    @Test
    void testEquality() {
        Barcode bc1 = Barcode.of("1");
        Barcode bc11 = Barcode.of("1");
        Barcode bc2 = Barcode.of("2");

        assertThat(bc1).isEqualTo(bc11);
        assertThat(bc11).isEqualTo(bc1);
        assertThat(bc1).isNotEqualTo(bc2);

        HashSet<Barcode> set = new HashSet<>();
        set.add(bc1);
        assertThat(set).hasSize(1);
        set.add(bc2);
        assertThat(set).hasSize(2);
        set.add(bc11);
        assertThat(set).hasSize(2);
    }
}
