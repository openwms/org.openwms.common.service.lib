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
package org.openwms.common.transport.barcode;

import org.ameba.annotation.Measured;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ConfiguredBarcodeFormat.
 *
 * @author Heiko Scherrer
 */
public class ConfiguredBarcodeFormatter implements BarcodeFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguredBarcodeFormatter.class);
    private  String pattern;
    private  String padder;
    private  String length;
    private  String alignment;
    private  String prefix;
    private  String suffix;

    public ConfiguredBarcodeFormatter() {
        this.pattern = System.getProperty("owms.common.barcode.pattern", "");
        this.padder = System.getProperty("owms.common.barcode.padder", String.valueOf(Barcode.PADDER));
        this.length = System.getProperty("owms.common.barcode.length", String.valueOf(Barcode.BARCODE_LENGTH));
        this.alignment = System.getProperty("owms.common.barcode.alignment", Barcode.BARCODE_ALIGN.RIGHT.name());
        this.prefix = System.getProperty("owms.common.barcode.prefix", "");
        this.suffix = System.getProperty("owms.common.barcode.suffix", "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public String format(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            LOGGER.debug("Barcode to format is null");
            return barcode;
        }
        StringBuilder result = "".equals(prefix) ? new StringBuilder() : new StringBuilder(prefix);
        if ("".equals(pattern)) {

            // check for property based configuration
            if ("".equals(padder)) {
                LOGGER.debug("No pattern, no padder - nothing to format. Barcode [{}]", barcode);
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
                LOGGER.debug("No pattern, formatted Barcode [{}]", result);
                return result.toString();
            }
        }
        // use pattern
        result.append(String.format(pattern, barcode));
        if (!"".equals(suffix)) {
            result.append(suffix);
        }
        LOGGER.debug("Format incoming Barcode [{}] into [{}]", barcode, result);
        return result.toString();
    }
}
