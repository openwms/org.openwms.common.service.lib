/*
 * Copyright 2005-2022 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


/**
 * A Barcode is a printable item with an unique identifier to label {@code TransportUnit}s. The identifier has a defined number of
 * characters whereas these characters are aligned either left or right. Non filled positions of a Barcode are padded with a so called
 * padding character.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 */
@Configurable(autowire = Autowire.BY_TYPE, preConstruction = true)
@Embeddable
public class Barcode implements Serializable {

    /** Length of a Barcode field. */
    public static final int BARCODE_LENGTH = 20;

    /**
     * A BARCODE_ALIGN defines whether the {@code Barcode} is applied {@code LEFT} or
     * {@code RIGHT}. Only be used when padding is activated.
     *
     * @author Heiko Scherrer
     */
    public enum BARCODE_ALIGN {
        /** Barcode is left aligned. */
        LEFT,
        /** Barcode is right aligned. */
        RIGHT
    }
    /**
     * Defines a character used for padding.<br> If the actually length of the {@code Barcode} is less than the maximum defined {@code
     * length} the rest will be filled with {@code padder} characters.
     */
    public static char PADDER = '0';

    /** 'Identifier' of the {@code Barcode}. <p> <i>Note:</i>It is not guaranteed that this field must be unique. </p> */
    @Column(name = "C_BARCODE")
    private String value;

    /*~ ----------------------------- constructors ------------------- */

    /** Dear JPA... */
    protected Barcode() {
    }

    private Barcode(String value) {
        this.value = value;
    }

    /**
     * Simple factory method to replace default constructor in application logic (still needed for framework stuff).
     *
     * @param value The value of the {@code Barcode} as String
     * @return Formatted Barcode instance
     */
    public static Barcode of(String value) {
        return new Barcode(value);
    }
    /*~ ----------------------------- methods ------------------- */
    /**
     * Return the {@code Barcode} value.
     *
     * @return The value of the {@code Barcode}
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the value of the {@code Barcode} as String.
     *
     * @return As String
     * @see #getValue()
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Barcode barcode = (Barcode) o;
        return value.equals(barcode.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}