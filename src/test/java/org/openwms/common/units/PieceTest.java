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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A PieceTest.
 * 
 * @author Heiko Scherrer
 */
class PieceTest {

    @Test void testCompareTo() {
        Piece p30 = new Piece(30);
        Piece p50 = new Piece(50, PieceUnit.PC);
        assertEquals(1, p50.compareTo(p30));
        assertEquals(-1, p30.compareTo(p50));
    }

    @Test void testConvertToPieceUnit() {
        Piece p30 = new Piece(30);
        Piece p50 = new Piece(50, PieceUnit.PC);

        Piece p502 = p50.convertTo(PieceUnit.DOZ);
        assertFalse(p502.equals(p50));

        assertTrue(p502.getMagnitude().equals(new BigDecimal(4)));
        assertTrue(p502.getUnitType() == PieceUnit.DOZ);

        assertTrue(p502.equals(new Piece(4, PieceUnit.DOZ)));
        assertFalse(p502.equals(new Piece(50, PieceUnit.PC)));
        assertTrue(p502.equals(new Piece(48, PieceUnit.PC)));
        assertTrue(p50.getUnitType() == PieceUnit.PC);

        assertEquals(1, p50.compareTo(p30));
        assertEquals(-1, p30.compareTo(p50));

        Piece p5doz = new Piece(5, PieceUnit.DOZ);
        assertEquals(1, p5doz.compareTo(p50));
        assertEquals(1, p5doz.compareTo(p30));
        assertEquals(-1, p50.compareTo(p5doz));
        assertEquals(-1, p30.compareTo(p5doz));

        Piece p60 = new Piece(60, PieceUnit.PC);
        assertEquals(0, p5doz.compareTo(p60));
        assertEquals(0, p60.compareTo(p5doz));
    }
}