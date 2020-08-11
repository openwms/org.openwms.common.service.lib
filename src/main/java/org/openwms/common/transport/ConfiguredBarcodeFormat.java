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

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * A ConfiguredBarcodeFormat.
 *
 * @author Heiko Scherrer
 */
@Component
public class ConfiguredBarcodeFormat implements BarcodeFormatProvider {

    private final String pattern = System.getProperty("owms.common.barcode-format", "%s");

    @Override
    public Optional<String> format(String barcode) {
        if ("".equals(pattern) || barcode == null || barcode.isEmpty()) {
           return Optional.empty();
        }
        return Optional.of(String.format(pattern, barcode));
    }
}
