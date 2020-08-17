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
package org.openwms.common.transport.barcode;

import org.ameba.annotation.Measured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

/**
 * A ConfiguredBarcodeFormat.
 *
 * @author Heiko Scherrer
 */
@Component
public class ConfiguredBarcodeFormatter implements BarcodeFormatter {

    private final String pattern = System.getProperty("owms.common.barcode.pattern", "");
    private final String padder = System.getProperty("owms.common.barcode.padder", String.valueOf(Barcode.PADDER));
    private final String length = System.getProperty("owms.common.barcode.length", String.valueOf(Barcode.BARCODE_LENGTH));
    private final String alignment = System.getProperty("owms.common.barcode.alignment", Barcode.BARCODE_ALIGN.RIGHT.name());
    private final String prefix = System.getProperty("owms.common.barcode.prefix", "");
    private final String suffix = System.getProperty("owms.common.barcode.suffix", "");

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public String format(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
           return barcode;
        }
        StringBuilder result = "".equals(prefix) ? new StringBuilder() : new StringBuilder(prefix);
        if ("".equals(pattern)) {

            // check for property based configuration
            if ("".equals(padder)) {
                return barcode;
            } else {

                result.append(
                        Barcode.BARCODE_ALIGN.RIGHT.name().equals(alignment)
                                ? StringUtils.leftPad(barcode, Integer.parseInt(length), padder)
                                : StringUtils.rightPad(barcode, Integer.parseInt(length), padder)
                );
                if (!"".equals(suffix)) {
                    result.append(suffix);
                }
                return result.toString();
            }
        }
        // use pattern
        result.append(String.format(pattern, barcode));
        if (!"".equals(suffix)) {
            result.append(suffix);
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Barcode convert(@NotEmpty String barcode) {
        return Barcode.of(barcode);
    }
}
