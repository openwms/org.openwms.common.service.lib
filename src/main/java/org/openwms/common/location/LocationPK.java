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
package org.openwms.common.location;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A LocationPK, is a value type and is used as an unique natural key for {@link Location} entities.
 *
 * @author Heiko Scherrer
 * @see Location
 */
@Embeddable
public class LocationPK implements Serializable {

    public static final short KEY_LENGTH = 4;
    public static final short NUMBER_OF_KEYS = 5;
    /**
     * Returns the complete length of all keys. Currently all keys have the same length, therefore it is the 5 times the length of a single
     * key (KEY_LENGTH). But since this can change the actual length is encapsulated within this method.
     */
    public static final short PK_LENGTH = NUMBER_OF_KEYS * KEY_LENGTH;

    /** Expresses the area the {@link Location} belongs to. */
    @Column(name = "C_AREA", nullable = false, length = KEY_LENGTH)
    @Size(max = KEY_LENGTH)
    private String area;

    /** Expresses the aisle the {@link Location} belongs to. */
    @Column(name = "C_AISLE", nullable = false, length = KEY_LENGTH)
    @Size(max = KEY_LENGTH)
    private String aisle;

    /** Expresses the x-dimension the {@link Location} belongs to. */
    @Column(name = "C_X", nullable = false, length = KEY_LENGTH)
    @Size(max = KEY_LENGTH)
    private String x;

    /** Expresses the y-dimension the {@link Location} belongs to. */
    @Column(name = "C_Y", nullable = false, length = KEY_LENGTH)
    @Size(max = KEY_LENGTH)
    private String y;

    /** Expresses the z-dimension the {@link Location} belongs to. */
    @Column(name = "C_Z", nullable = false, length = KEY_LENGTH)
    @Size(max = KEY_LENGTH)
    private String z;

    /*~ ----------------------------- constructors ------------------- */

    /** Dear JPA ... */
    protected LocationPK() { }

    /**
     * Create a new LocationPK with all required fields.
     *
     * @param area Area where the {@link Location} belongs to
     * @param aisle Aisle where the {@link Location} belongs to
     * @param x Dimension x where the {@link Location} belongs to
     * @param y Dimension y where the {@link Location} belongs to
     * @param z Dimension z where the {@link Location} belongs to
     */
    public static LocationPK of(String area, String aisle, String x, String y, String z) {
        return new LocationPK(area, aisle, x, y, z);
    }

    /**
     * Weak constructor to create a new LocationPK with a couple of keys only.
     *
     * @param keys The array of keys, currently expected to be 5
     * @throws IllegalArgumentException if the number of keys does not match {@link LocationPK#NUMBER_OF_KEYS}
     */
    private LocationPK(String... keys) {
        if (keys == null || keys.length != NUMBER_OF_KEYS) {
            throw new IllegalArgumentException(
                    "Number of key fields to create a LocationPK does not match the defined number of keys. Expected: " + NUMBER_OF_KEYS);
        }
        this.area = keys[0];
        this.aisle = keys[1];
        this.x = keys[2];
        this.y = keys[3];
        this.z = keys[4];
    }

    /**
     * Create a {@link LocationPK} from the given String.
     *
     * @param s The String, not {@literal null}
     * @return An instance
     */
    public static LocationPK fromString(String s) {
        Assert.hasText(s, "Location String must not be null");
        return new LocationPK(s.split("/"));
    }

    /**
     * Checks whether the given {@code locationPK} String is in valid format.
     *
     * @param locationPk The String to verify
     * @return {@literal true} if valid
     */
    public static boolean isValid(String locationPk) {
        return locationPk != null && locationPk.split("/").length == NUMBER_OF_KEYS;
    }
    /*~ ----------------------------- methods ------------------- */

    /**
     * Get the area region.
     *
     * @return The area
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Get the aisle region.
     *
     * @return The aisle
     */
    public String getAisle() {
        return this.aisle;
    }

    /**
     * Get the x-dimension.
     *
     * @return The x-dimension
     */
    public String getX() {
        return this.x;
    }

    /**
     * Get the y-dimension.
     *
     * @return The y-dimension
     */
    public String getY() {
        return this.y;
    }

    /**
     * Get the z-dimension.
     *
     * @return The z-dimension
     */
    public String getZ() {
        return this.z;
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocationPK other)) {
            return false;
        }
        return this.y.equals(other.y) && this.x.equals(other.x) && this.area.equals(other.area)
                && this.z.equals(other.z) && this.aisle.equals(other.aisle);
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.y.hashCode() ^ this.x.hashCode() ^ this.area.hashCode() ^ this.z.hashCode() ^ this.aisle.hashCode();
    }

    /**
     * Return a String like AREA/AISLE/X/Y/Z.
     *
     * @return String
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return this.area + "/" + this.aisle + "/" + this.x + "/" + this.y + "/" + this.z;
    }
}