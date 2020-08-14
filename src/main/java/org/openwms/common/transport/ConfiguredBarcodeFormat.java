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

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * A ConfiguredBarcodeFormat.
 *
 * @author Heiko Scherrer
 */
public class ConfiguredBarcodeFormat implements BarcodeFormatProvider {

    private final String pattern = System.getProperty("owms.common.barcode.pattern", "");
    private final String padder = System.getProperty("owms.common.barcode.padder", "");
    private final String length = System.getProperty("owms.common.barcode.length", String.valueOf(Barcode.BARCODE_LENGTH));
    private final String alignment = System.getProperty("owms.common.barcode.alignment", Barcode.BARCODE_ALIGN.RIGHT.name());
    private final String prefix = System.getProperty("owms.common.barcode.prefix", "");
    private final String suffix = System.getProperty("owms.common.barcode.suffix", "");

    @Override
    public Optional<String> format(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
           return Optional.empty();
        }
        if ("".equals(pattern)) {

            // check for property based configuration
            if ("".equals(padder)) {
                return Optional.empty();
            } else {

                return Optional.of(Barcode.BARCODE_ALIGN.RIGHT.name().equals(alignment)
                        ? StringUtils.leftPad(barcode, Integer.parseInt(length), padder)
                        : StringUtils.rightPad(barcode, Integer.parseInt(length), padder)
                );
            }
        }
        // use pattern
        return Optional.of(String.format(pattern, barcode));
    }
}
