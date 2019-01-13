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

import java.util.Arrays;
import java.util.List;

/**
 * A WeightUnit is a concrete set of all possible weights.
 * <p>
 * In SI format.
 * </p>
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public enum WeightUnit implements BaseUnit<WeightUnit>, BaseUnitVO<WeightUnit> {

    /**
     * Milligram.
     */
    MG(1000000000),
    /**
     * Gram.
     */
    G(1000000),
    /**
     * Kilogram.
     */
    KG(1000),
    /**
     * Tons.
     */
    T(1);

    private Long magnitude;
    private static WeightUnit[] all = { WeightUnit.MG, WeightUnit.G, WeightUnit.KG, WeightUnit.T };

    /**
     * Create a new <code>WeightUnit</code>.
     * 
     * @param magnitude
     *            The base unit of the weight
     */
    WeightUnit(long magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Get the magnitude of this <code>PieceUnit</code>.
     * 
     * @return the magnitude
     */
    public Long getMagnitude() {
        return this.magnitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WeightUnit> getAll() {
        return Arrays.asList(all);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WeightUnit getBaseUnit() {
        return G;
    }
}