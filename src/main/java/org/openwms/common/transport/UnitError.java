/*
 * Copyright 2005-2021 the original author or authors.
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

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * An UnitError represents an error occurring on a {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_UNIT_ERROR")
public class UnitError extends ApplicationEntity implements Serializable {

    /** Separator to use in toString method. */
    static final String SEPARATOR = "::";

    /** Error number. */
    @Column(name = "C_ERROR_NO")
    private String errorNo;

    /** Error message text. */
    @Column(name = "C_ERROR_TEXT")
    private String errorText;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "C_TU_ID", foreignKey = @ForeignKey(name = "FK_TU_ERROR_PK"))
    private TransportUnit tu;

    /*~ ----------------------------- constructors ------------------- */

    /**
     * Dear JPA...
     */
    UnitError() {
    }

    private UnitError(Builder builder) {
        this.errorNo = builder.errorNo;
        this.errorText = builder.errorText;
        this.tu = builder.tu;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /*~ ----------------------------- accessors ------------------- */
    public String getErrorNo() {
        return errorNo;
    }

    public String getErrorText() {
        return errorText;
    }

    /**
     * Set the TransportUnit for this error.
     *
     * @param tu The TransportUnit instance
     */
    void setTu(TransportUnit tu) {
        this.tu = tu;
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
        private TransportUnit tu;

        private Builder() {
        }

        public Builder errorNo(String val) {
            errorNo = val;
            return this;
        }

        public Builder errorText(String val) {
            errorText = val;
            return this;
        }

        public Builder tu(TransportUnit val) {
            tu = val;
            return this;
        }

        public UnitError build() {
            return new UnitError(this);
        }
    }
}