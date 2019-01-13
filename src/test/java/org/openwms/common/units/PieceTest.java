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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * A PieceTest.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class PieceTest {

    @Test
    public final void testCompareTo() {
        Piece p30 = new Piece(30);
        Piece p50 = new Piece(50, PieceUnit.PC);
        Assert.assertEquals(1, p50.compareTo(p30));
        Assert.assertEquals(-1, p30.compareTo(p50));
    }

    @Test
    public final void testConvertToPieceUnit() throws Exception {
        Piece p30 = new Piece(30);
        Piece p50 = new Piece(50, PieceUnit.PC);
        ObjectMapper om = new ObjectMapper();
        System.out.println(om.writeValueAsString(p50));

        Piece p502 = p50.convertTo(PieceUnit.DOZ);
        Assert.assertFalse(p502.equals(p50));

        Assert.assertTrue(p502.getMagnitude().equals(new BigDecimal(4)));
        Assert.assertTrue(p502.getUnitType() == PieceUnit.DOZ);

        Assert.assertTrue(p502.equals(new Piece(4, PieceUnit.DOZ)));
        Assert.assertFalse(p502.equals(new Piece(50, PieceUnit.PC)));
        Assert.assertTrue(p502.equals(new Piece(48, PieceUnit.PC)));
        Assert.assertTrue(p50.getUnitType() == PieceUnit.PC);

        Assert.assertEquals(1, p50.compareTo(p30));
        Assert.assertEquals(-1, p30.compareTo(p50));

        Piece p5doz = new Piece(5, PieceUnit.DOZ);
        Assert.assertEquals(1, p5doz.compareTo(p50));
        Assert.assertEquals(1, p5doz.compareTo(p30));
        Assert.assertEquals(-1, p50.compareTo(p5doz));
        Assert.assertEquals(-1, p30.compareTo(p5doz));

        Piece p60 = new Piece(60, PieceUnit.PC);
        Assert.assertEquals(0, p5doz.compareTo(p60));
        Assert.assertEquals(0, p60.compareTo(p5doz));
    }
}