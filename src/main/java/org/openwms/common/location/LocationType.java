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
package org.openwms.common.location;

import org.ameba.integration.jpa.ApplicationEntity;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LocationType defines a type of {@code Location}s with same characteristics.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 * @see Location
 */
@Entity
@Table(name = "COM_LOCATION_TYPE")
public class LocationType extends ApplicationEntity implements Serializable {
    
    /** Type of the {@code LocationType} (unique). */
    @Column(name = "C_TYPE", unique = true, nullable = false)
    @OrderBy
    private String type;

    /** Description of the {@code LocationType}. */
    @Column(name = "C_DESCRIPTION")
    private String description = DEF_TYPE_DESCRIPTION;
    /** Default value of the description, by default * {@value} . */
    public static final String DEF_TYPE_DESCRIPTION = "--";

    /** Length of the {@code LocationType}. */
    @Column(name = "C_LENGTH")
    private int length = DEF_LENGTH;
    /** Default value of {@link #length}. */
    public static final int DEF_LENGTH = 0;

    /** Width of the {@code LocationType}. */
    @Column(name = "C_WIDTH")
    private int width = DEF_WIDTH;
    /** Default value of {@link #width}. */
    public static final int DEF_WIDTH = 0;

    /** Height of the {@code LocationType}. */
    @Column(name = "C_HEIGHT")
    private int height = DEF_HEIGHT;
    /** Default value of {@link #height}. */
    public static final int DEF_HEIGHT = 0;

    /*~ ----------------------------- constructors ------------------- */
    /**
     * Dear JPA...
     */
    protected LocationType() {
    }

    /**
     * Create a new {@code LocationType} with an unique natural key.
     *
     * @param type Unique type
     */
    public LocationType(String type) {
        Assert.hasText(type, "type must exist when creating a new LocationType");
        this.type = type;
    }

    /* ----------------------------- methods ------------------- */
    /**
     * Returns the unique identifier of the {@code LocationType}.
     *
     * @return type The Type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the length of the {@code LocationType}.
     *
     * @return length The Length
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Set the length of this {@code LocationType}.
     *
     * @param length The length of this type
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns the width of this {@code LocationType}.
     *
     * @return width The Width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Set the width of this {@code LocationType}.
     *
     * @param width The width of this type
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the height of the {@code LocationType}.
     *
     * @return height The Height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Set the height of this {@code LocationType}.
     *
     * @param height The height of this type
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the description of this {@code LocationType}.
     *
     * @return description The description text
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the {@code LocationType}.
     *
     * @param description The description text of this type
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the type as String.
     *
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationType that = (LocationType) o;
        return Objects.equals(type, that.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}