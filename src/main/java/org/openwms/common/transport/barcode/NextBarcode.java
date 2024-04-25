/*
 * Copyright 2005-2024 the original author or authors.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.ameba.integration.jpa.BaseEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * A NextBarcode.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_BARCODE", uniqueConstraints =
    @UniqueConstraint(name = "UC_BARCODE_NAME", columnNames = {"C_NAME"})
)
public class NextBarcode extends BaseEntity implements Serializable {

    /** Name of the Account. */
    @Column(name = "C_NAME")
    private String name;
    /** Last given Barcode. */
    @Column(name = "C_CURRENT", length = 40)
    private String currentBarcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentBarcode() {
        return currentBarcode;
    }

    public void setCurrentBarcode(String currentBarcode) {
        this.currentBarcode = currentBarcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NextBarcode that = (NextBarcode) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(currentBarcode, that.currentBarcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, currentBarcode);
    }

    @Override
    public String toString() {
        return currentBarcode;
    }
}
