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
import org.openwms.common.transport.Barcode.BARCODE_ALIGN;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * A BarcodeTest.
 * 
 * @author Heiko Scherrer
 */
class BarcodeTest {

    {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode-format", "%1$20s");
    }

    @Test void testCreation() {
        assertThrows(IllegalArgumentException.class, () -> Barcode.of(null));
        new Barcode();
        Barcode.of("TEST");
    }

    @Test void testBarcode() {
        Barcode test = Barcode.of("TEST");
        assertThat(Barcode.isPadded()).isTrue();
        assertThat(test.getValue()).isEqualTo("0000000000000000TEST");
        assertThat(Barcode.getAlignment()).isEqualTo(BARCODE_ALIGN.RIGHT);

        Barcode.setPadded(false);
        assertThat(Barcode.isPadded()).isFalse();

        Barcode.setLength(20);
        Barcode.setPadder('0');
        assertThat(Barcode.getPadder()).isEqualTo('0');
        assertThat(Barcode.isPadded()).isTrue();

        Barcode bc3 = Barcode.of("RIGHT");
        assertTrue("Barcode length must be expanded to 20 characters.", (20 == Barcode.getLength()));
        assertThat(bc3.getValue()).isEqualTo("000000000000000RIGHT");
        assertThat(bc3.toString()).isEqualTo("000000000000000RIGHT");

        Barcode.setAlignment(BARCODE_ALIGN.LEFT);
        Barcode bc2 = Barcode.of("LEFT");
        assertThat(bc2.getValue()).isEqualTo("LEFT0000000000000000");

        Barcode.setLength(2);
        Barcode bc4 = Barcode.of("A123456789");
        assertThat(bc4.getValue()).isEqualTo("A123456789");

        // Reset static fields !!
        Barcode.setAlignment(BARCODE_ALIGN.RIGHT);
        Barcode.setLength(20);
        Barcode.setPadded(false);
    }

    @Test void testEquality() {
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
