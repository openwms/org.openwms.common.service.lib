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
import org.ameba.annotation.TxService;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.openwms.common.CommonConstants.DEFAULT_ACCOUNT_NAME;

/**
 * A NumericBarcodeGenerator.
 *
 * @author Heiko Scherrer
 */
@TxService
public class NumericBarcodeGenerator implements BarcodeGenerator {

    protected final NextBarcodeRepository repository;
    protected final BarcodeFormatter formatter;

    public NumericBarcodeGenerator(NextBarcodeRepository repository, BarcodeFormatter formatter) {
        this.repository = repository;
        this.formatter = formatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Barcode convert(String barcode) {
        return Barcode.of(formatter.format(barcode));
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
            return Barcode.of(formatter.format("1"));
        }
        NextBarcode nextBarcode = aDefault.get();
        int current = Integer.parseInt(nextBarcode.getCurrentBarcode());
        String result = String.valueOf(++current);
        nextBarcode.setCurrentBarcode(result);
        return Barcode.of(formatter.format(result));
    }
}
