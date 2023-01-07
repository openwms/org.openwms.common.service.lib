/*
 * Copyright 2005-2023 the original author or authors.
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * A BarcodeGenerator.
 *
 * @author Heiko Scherrer
 */
public interface BarcodeGenerator {

    /**
     * Generate a new {@link Barcode}.
     *
     * Depends on the underlying implementation whether a new {@link Barcode} is created everytime the method is called or not.
     * @param transportUnitType The known type of the TransportUnitType
     * @param actualLocation The known actual Location
     * @return A new Barcode instance, never {@literal null}
     */
    @NotNull Barcode generate(String transportUnitType, String actualLocation);

    /**
     * Convert the given {@code barcode}.
     *
     * @param barcode The Barcode to convert
     * @return An Optional with the formatted barcode as a value if the formatting could be performed or an empty Optional if not
     */
    Barcode convert(@NotEmpty String barcode);
}
