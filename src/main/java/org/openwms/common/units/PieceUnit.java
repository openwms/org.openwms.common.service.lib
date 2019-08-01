/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.units;

import org.openwms.core.values.BaseUnit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * A PieceUnit.
 * 
 * @author Heiko Scherrer
 */
public enum PieceUnit implements BaseUnit<PieceUnit> {

    /** A Piece. */
    PC(new BigDecimal(1)),

    /** A Dozen. */
    DOZ(new BigDecimal(12));

    private BigDecimal magnitude;
    private static PieceUnit[] all = { PieceUnit.PC, PieceUnit.DOZ };

    /**
     * Get the magnitude of this <code>PieceUnit</code>.
     * 
     * @return the magnitude
     */
    public BigDecimal getMagnitude() {
        return this.magnitude;
    }

    /**
     * Create a new <code>PieceUnit</code>.
     * 
     * @param magnitude
     *            The base unit of the weight
     */
    PieceUnit(BigDecimal magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PieceUnit> getAll() {
        return Arrays.asList(all);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PieceUnit getBaseUnit() {
        return PC;
    }
}