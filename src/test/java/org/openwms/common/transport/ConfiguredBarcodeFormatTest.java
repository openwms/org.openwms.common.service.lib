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
import org.openwms.common.transport.barcode.ConfiguredBarcodeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A ConfiguredBarcodeFormatTest.
 *
 * @author Heiko Scherrer
 */
class ConfiguredBarcodeFormatTest {

    @Test
    void test() {
        System.setProperty("owms.common.barcode.pattern", "0000%s1111");
        ConfiguredBarcodeFormatter cbf = new ConfiguredBarcodeFormatter();
        String format = cbf.format(" 4711 ");
        assertThat(format).isEqualTo("0000 4711 1111");
        System.setProperty("owms.common.barcode.pattern", "");
    }

}