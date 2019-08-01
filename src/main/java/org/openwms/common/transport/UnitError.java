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
package org.openwms.common.transport;

import org.ameba.integration.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * An UnitError represents an error occurring on {@code TransportUnit}s, on {@code LoadUnit}s or others.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_UNIT_ERROR")
public class UnitError extends BaseEntity implements Serializable {

    /** Separator to use in toString method. */
    static final String SEPARATOR = "::";

    /** Error number. */
    @Column(name = "C_ERROR_NO")
    private String errorNo;

    /** Error message text. */
    @Column(name = "C_ERROR_TEXT")
    private String errorText;

    /*~ ----------------------------- constructors ------------------- */

    /**
     * Dear JPA...
     */
    UnitError() {
    }

    private UnitError(Builder builder) {
        setErrorNo(builder.errorNo);
        setErrorText(builder.errorText);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /*~ ----------------------------- methods ------------------- */

    /**
     * Return the error number.
     *
     * @return The error number
     */
    public String getErrorNo() {
        return errorNo;
    }

    /**
     * Set the error number.
     *
     * @param errorNo The errorNo to set.
     */
    public void setErrorNo(String errorNo) {
        this.errorNo = errorNo;
    }

    /**
     * Return the error text.
     *
     * @return The error text
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Set the error text.
     *
     * @param errorText The errorText to set.
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitError unitError = (UnitError) o;
        return Objects.equals(errorNo, unitError.errorNo) &&
                Objects.equals(errorText, unitError.errorText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(errorNo, errorText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return errorNo + SEPARATOR + errorText;
    }


    /**
     * {@code UnitError} builder static inner class.
     */
    public static final class Builder {

        private String errorNo;
        private String errorText;

        private Builder() {
        }

        /**
         * Sets the {@code errorNo} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code errorNo} to set
         * @return a reference to this Builder
         */
        public Builder errorNo(String val) {
            errorNo = val;
            return this;
        }

        /**
         * Sets the {@code errorText} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code errorText} to set
         * @return a reference to this Builder
         */
        public Builder errorText(String val) {
            errorText = val;
            return this;
        }

        /**
         * Returns a {@code UnitError} built from the parameters previously set.
         *
         * @return a {@code UnitError} built with parameters of this {@code UnitError.Builder}
         */
        public UnitError build() {
            return new UnitError(this);
        }
    }
}