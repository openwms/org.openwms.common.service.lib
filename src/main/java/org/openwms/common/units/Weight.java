/*
 * Copyright 2005-2019 the original author or authors.
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

import org.openwms.core.values.Measurable;
import org.openwms.core.values.UnitType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Weight represents a real world weight, that comes with an <code>Unit</code> and a value.
 * 
 * @author Heiko Scherrer
 */
public class Weight implements Measurable<BigDecimal, Weight, WeightUnit>, UnitType, Serializable {

    /** The unit of the <code>Weight</code>. */
    private WeightUnit unit;
    /** The magnitude of the <code>Weight</code>. */
    private BigDecimal magnitude;
    /** Constant for a zero value. */
    public static final Weight ZERO = new Weight("0");

    /* ----------------------------- methods ------------------- */
    /**
     * Accessed by persistence provider.
     */
    protected Weight() {
        super();
    }

    /**
     * Create a new <code>Weight</code>.
     * 
     * @param magnitude
     *            The magnitude of the <code>Weight</code>
     * @param unit
     *            The unit of measure
     */
    public Weight(BigDecimal magnitude, WeightUnit unit) {
        this.magnitude = magnitude;
        this.unit = unit;
    }

    /**
     * Create a new <code>Weight</code>.
     * 
     * @param magnitude
     *            The magnitude of the <code>Weight</code> as String
     * @param unit
     *            The unit of measure
     */
    public Weight(String magnitude, WeightUnit unit) {
        this.magnitude = new BigDecimal(magnitude);
        this.unit = unit;
    }

    /**
     * Create a new <code>Weight</code>.
     * 
     * @param magnitude
     *            The magnitude of the <code>Weight</code>
     */
    public Weight(BigDecimal magnitude) {
        this.magnitude = magnitude;
        this.unit = WeightUnit.T.getBaseUnit();
    }

    /**
     * Create a new <code>Weight</code>.
     * 
     * @param magnitude
     *            The magnitude of the <code>Weight</code> as String
     */
    public Weight(String magnitude) {
        this.magnitude = new BigDecimal(magnitude);
        this.unit = WeightUnit.T.getBaseUnit();
    }

    /**
     * @see org.openwms.core.values.UnitType#getMeasurable()
     */
    @Override
    public Weight getMeasurable() {
        return this;
    }

    /**
     * Returns the magnitude of the <code>Weight</code>.
     * 
     * @return The magnitude
     */
    @Override
    public BigDecimal getMagnitude() {
        return magnitude;
    }

    /**
     * Returns the unit of the <code>Weight</code>.
     * 
     * @return The unit
     */
    @Override
    public WeightUnit getUnitType() {
        return unit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isZero() {
        return this.getMagnitude().equals(BigDecimal.ZERO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNegative() {
        return this.getMagnitude().signum() == -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Weight convertTo(WeightUnit unt) {
        return new Weight(getMagnitude().scaleByPowerOfTen((this.getUnitType().ordinal() - unt.ordinal()) * 3), unt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Weight o) {
        if (o.getUnitType().ordinal() > this.getUnitType().ordinal()) {
            return -1;
        } else if (o.getUnitType().ordinal() < this.getUnitType().ordinal()) {
            return 1;
        } else {
            return this.getMagnitude().compareTo(o.getMagnitude());
        }
    }

    /**
     * Use amount and unit for calculation.
     * 
     * @return The hashCode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMagnitude() == null) ? 0 : getMagnitude().hashCode());
        result = prime * result + ((getUnitType() == null) ? 0 : getUnitType().hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * Use amount and unit for comparison.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Weight other = (Weight) obj;
        if (getMagnitude() == null) {
            if (other.getMagnitude() != null) {
                return false;
            }
        } else if (!getMagnitude().equals(other.getMagnitude())) {
            return false;
        }
        if (getUnitType() != other.getUnitType()) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getMagnitude() + " " + getUnitType();
    }
}