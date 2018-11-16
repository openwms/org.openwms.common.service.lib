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

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * A WeightTest.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class WeightTest {

    /**
     * Test creation of a Weight.
     */
    @Test
    public final void testWeight() {
        Weight w1 = new Weight(new BigDecimal(1), WeightUnit.KG);
        Weight w2 = new Weight(new BigDecimal(1), WeightUnit.T);
        w2 = w2.convertTo(WeightUnit.KG);
        assertEquals(BigDecimal.ONE, w1.getMagnitude());
        assertEquals(new BigDecimal(0), new BigDecimal("1000").subtract(w2.getMagnitude()));
        assertEquals(w2.getUnitType(), WeightUnit.KG);
        w1.compareTo(w2);
    }

    /**
     * Test creation of a Weight and comparison.
     */
    @Test
    public final void testWeightComparison() {
        Weight w1 = new Weight(new BigDecimal(1), WeightUnit.G);
        Weight w2 = new Weight(new BigDecimal(1), WeightUnit.T);
        assertEquals("1G is less than 1T", -1, w1.compareTo(w2));
        assertEquals("1T is greater than 1G", 1, w2.compareTo(w1));

        Weight w3 = new Weight(new BigDecimal(2), WeightUnit.G);
        assertEquals("1G is less than 2G", -1, w1.compareTo(w3));
        assertEquals("2G is greater than 1G", 1, w3.compareTo(w1));

        Weight w4 = new Weight(new BigDecimal("0.000002"), WeightUnit.T);
        w3 = w3.convertTo(WeightUnit.T);
        assertEquals("2G are the same as 0.000002T", 0, w3.compareTo(w4));
    }
}
