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
package org.openwms.common.transport.barcode;

import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.ameba.annotation.Public;
import org.ameba.annotation.TxService;

import java.util.Optional;
import java.util.ServiceLoader;

import static org.openwms.common.CommonConstants.DEFAULT_ACCOUNT_NAME;

/**
 * A NumericBarcodeGenerator.
 *
 * @author Heiko Scherrer
 */
@Public("Because this class is also be instantiated by ServiceLoader not only by Spring")
@TxService
public class NumericBarcodeGenerator implements BarcodeGenerator {

    protected final NextBarcodeRepository repository;

    public NumericBarcodeGenerator(NextBarcodeRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Barcode convert(String barcode) {
        return Barcode.of(getFormatter().format(barcode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull Barcode generate(String transportUnitType, String actualLocation) {
        Optional<NextBarcode> aDefault = repository.findByName(DEFAULT_ACCOUNT_NAME);
        if (aDefault.isEmpty()) {
            NextBarcode nb = new NextBarcode();
            nb.setName(DEFAULT_ACCOUNT_NAME);
            nb.setCurrentBarcode("1");
            repository.save(nb);
            return Barcode.of(getFormatter().format("1"));
        }
        NextBarcode nextBarcode = aDefault.get();
        int current = Integer.parseInt(nextBarcode.getCurrentBarcode());
        String result = String.valueOf(++current);
        nextBarcode.setCurrentBarcode(result);
        return Barcode.of(getFormatter().format(result));
    }

    private BarcodeFormatter getFormatter() {
        return ServiceLoader.load(BarcodeFormatter.class).findFirst().orElseThrow(() -> new IllegalStateException("No BarcodeFormatter provider configured for ServiceLoader"));
    }
}
