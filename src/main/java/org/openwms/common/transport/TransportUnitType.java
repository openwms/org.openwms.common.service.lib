/*
 * Copyright 2005-2020 the original author or authors.
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
import org.openwms.common.location.LocationType;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A TransportUnitType is a type of a certain {@code TransportUnit}. Typically to store characteristic attributes of {@code TransportUnit}s,
 * such as the length, the height, or the weight of {@code TransportUnit}s. It is possible to group and characterize {@code TransportUnit}s.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 * @see TransportUnit
 */
@Entity
@Table(name = "COM_TRANSPORT_UNIT_TYPE")
public class TransportUnitType extends ApplicationEntity implements Serializable {

    /** Unique natural key. */
    @Column(name = "C_TYPE", unique = true, nullable = false)
    @OrderBy
    private String type;

    /** Description for this type. */
    @Column(name = "C_DESCRIPTION")
    private String description = DEF_TYPE_DESCRIPTION;
    /** Default description of the {@code TransportUnitType}. Default value}. */
    public static final String DEF_TYPE_DESCRIPTION = "--";

    /** Length of the {@code TransportUnitType}. */
    @Column(name = "C_LENGTH")
    private int length = DEF_LENGTH;
    /** Default value of {@link #length}. */
    public static final int DEF_LENGTH = 0;

    /** Width of the {@code TransportUnitType}. */
    @Column(name = "C_WIDTH")
    private int width = 0;
    /** Default value of {@link #width}. */
    public static final int DEF_WIDTH = 0;

    /** Height of the {@code TransportUnitType}. */
    @Column(name = "C_HEIGHT")
    private int height = 0;
    /** Default value of {@link #height}. */
    public static final int DEF_HEIGHT = 0;

    /** Tare weight of the {@code TransportUnitType}. */
    @Column(name = "C_WEIGHT_TARE", scale = 3)
    private BigDecimal weightTare;

    /** Maximum allowed weight of the {@code TransportUnitType}. */
    @Column(name = "C_WEIGHT_MAX", scale = 3)
    private BigDecimal weightMax;

    /** Effective weight of the {@code TransportUnitType}'s payload. */
    @Column(name = "C_PAYLOAD", scale = 3)
    private BigDecimal payload;

    /**
     * Characteristic used to hold specific compatibility attributes.<br /> Example:<br /> 'isn't compatible with...' or 'is compatible with
     * ...' or 'type owns another type ...'
     */
    @Column(name = "C_COMPATIBILITY")
    private String compatibility;

    /*~ ------------------- collection mapping ------------------- */
    /** A collection of all {@link TransportUnit}s belonging to this type. */
    @OneToMany(mappedBy = "transportUnitType")
    private Set<TransportUnit> transportUnits = new HashSet<>();

    /** Describes other {@code TransportUnitType}s and how many of these may be stacked on the {@code TransportUnitType}. */
    @OneToMany(mappedBy = "baseTransportUnitType", cascade = {CascadeType.ALL})
    private Set<TypeStackingRule> typeStackingRules = new HashSet<>();

    /** A Set of {@link TypePlacingRule}s store all possible {@code LocationType} s of the {@code TransportUnitType}. */
    @OneToMany(mappedBy = "transportUnitType", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("privilegeLevel")
    private Set<TypePlacingRule> typePlacingRules = new HashSet<>();

    /*~ ----------------------------- constructors ------------------- */
    /** Dear JPA... */
    protected TransportUnitType() { }

    /**
     * Create a new {@code TransportUnitType}.
     *
     * @param type Unique name
     */
    public TransportUnitType(String type) {
        Assert.hasText(type, "Type must not be null when creating a TransportUnitType");
        this.type = type;
    }

    private TransportUnitType(Builder builder) {
        this.type = builder.type;
        setDescription(builder.description);
        setLength(builder.length);
        setWidth(builder.width);
        setHeight(builder.height);
        setWeightTare(builder.weightTare);
        setWeightMax(builder.weightMax);
        setPayload(builder.payload);
        setCompatibility(builder.compatibility);
        setTransportUnits(builder.transportUnits);
        setTypeStackingRules(builder.typeStackingRules);
        setTypePlacingRules(builder.typePlacingRules);
    }

    /**
     * Create a TransportUnitType with the corresponding builder.
     *
     * @param type The business key
     * @return The builder instance
     */
    public static Builder newBuilder(String type) {
        return new Builder(type);
    }

    /*~ ----------------------------- methods ------------------- */

    /**
     * Factory method to create a new TransportUnitType.
     *
     * @param type The business key
     * @return The instance
     */
    public static TransportUnitType of(String type) {
        return new TransportUnitType(type);
    }

    /**
     * Returns the type of the {@code TransportUnitType}.
     *
     * @return The type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the width of the {@code TransportUnitType}.
     *
     * @return The width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Set the width of the {@code TransportUnitType}.
     *
     * @param width The width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the height of the {@code TransportUnitType}.
     *
     * @return The height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Set the height of the {@code TransportUnitType}.
     *
     * @param height The height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the length of the {@code TransportUnitType}.
     *
     * @return The length
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Set the length of the {@code TransportUnitType}.
     *
     * @param length The length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns the description of the {@code TransportUnitType}.
     *
     * @return The description text
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description for the {@code TransportUnitType}.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the payload of the {@code TransportUnitType}.
     *
     * @return The payload
     */
    public BigDecimal getPayload() {
        return this.payload;
    }

    /**
     * Set the payload of the {@code TransportUnitType}.
     *
     * @param payload The payload to set
     */
    public void setPayload(BigDecimal payload) {
        this.payload = payload;
    }

    /**
     * Returns the compatibility of the {@code TransportUnitType}.
     *
     * @return The compatibility
     */
    public String getCompatibility() {
        return this.compatibility;
    }

    /**
     * Set the compatibility of the {@code TransportUnitType}.
     *
     * @param compatibility The compatibility to set
     */
    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    /**
     * Returns a Set of all {@link TransportUnit}s belonging to the {@code TransportUnitType}.
     *
     * @return A Set of all {@link TransportUnit}s belonging to the {@code TransportUnitType}
     */
    public Set<TransportUnit> getTransportUnits() {
        return this.transportUnits;
    }

    /**
     * Assign a Set of {@link TransportUnit}s to the {@code TransportUnitType}. Already existing {@link TransportUnit}s will be removed.
     *
     * @param transportUnits A Set of {@link TransportUnit}s.
     */
    private void setTransportUnits(Set<TransportUnit> transportUnits) {
        this.transportUnits = transportUnits;
    }

    /**
     * Add a rule to the {@code TransportUnitType}. A {@link TypePlacingRule} determines what {@code TransportUnitType}s can be placed on
     * which locations.
     *
     * @param typePlacingRule The rule to set
     * @return {@literal true} when the rule was added gracefully, otherwise {@literal false}
     */
    public boolean addTypePlacingRule(TypePlacingRule typePlacingRule) {
        Assert.notNull(typePlacingRule, "typePlacingRule to add is null, this: " + this);
        return typePlacingRules.add(typePlacingRule);
    }

    /**
     * Remove a {@link TypePlacingRule} from the collection or rules.
     *
     * @param typePlacingRule The rule to be removed
     * @return {@literal true} when the rule was removed gracefully, otherwise {@literal false}
     */
    public boolean removeTypePlacingRule(TypePlacingRule typePlacingRule) {
        Assert.notNull(typePlacingRule, "typePlacingRule to remove is null, this: " + this);
        return typePlacingRules.remove(typePlacingRule);
    }

    /**
     * Remove a {@link TypePlacingRule} from the collection or rules.
     *
     * @param locationType All TypePlacingRules of this LocationType are going to be removed
     */
    public void removeTypePlacingRules(LocationType locationType) {
        Assert.notNull(locationType, "locationType to remove is null, this: " + this);
        List<TypePlacingRule> toRemove = typePlacingRules
                .stream()
                .filter(tpr -> tpr.getAllowedLocationType().getType().equals(locationType.getType()))
                .collect(Collectors.toList());
        typePlacingRules.removeAll(toRemove);
    }

    /**
     * Returns all {@link TypePlacingRule}s belonging to the {@code TransportUnitType}.
     *
     * @return A Set of all placing rules
     */
    public Set<TypePlacingRule> getTypePlacingRules() {
        return Collections.unmodifiableSet(this.typePlacingRules);
    }

    /**
     * Assign a Set of {@link TypePlacingRule}s to the {@code TransportUnitType}. Already existing {@link TypePlacingRule}s will be
     * removed.
     *
     * @param typePlacingRules The rules to set
     */
    private void setTypePlacingRules(Set<TypePlacingRule> typePlacingRules) {
        this.typePlacingRules = typePlacingRules;
    }

    /**
     * Returns a Set of all {@link TypeStackingRule}s. A {@link TypeStackingRule} determines which other {@code TransportUnitType}s can be
     * placed on the {@code TransportUnitType}.
     *
     * @return A Set of all stacking rules
     */
    public Set<TypeStackingRule> getTypeStackingRules() {
        return this.typeStackingRules;
    }

    /**
     * Assign a Set of {@link TypeStackingRule}s. A {@link TypeStackingRule} determines which {@code TransportUnitType}s can be placed on
     * the {@code TransportUnitType}. Already existing {@link TypeStackingRule} s will be removed.
     *
     * @param typeStackingRules The rules to set
     */
    private void setTypeStackingRules(Set<TypeStackingRule> typeStackingRules) {
        this.typeStackingRules = typeStackingRules;
    }

    /**
     * Get the weightTare.
     *
     * @return The weightTare.
     */
    public BigDecimal getWeightTare() {
        return weightTare;
    }

    /**
     * Set the weightTare.
     *
     * @param weightTare The weightTare to set.
     */
    public void setWeightTare(BigDecimal weightTare) {
        this.weightTare = weightTare;
    }

    /**
     * Get the weightMax.
     *
     * @return The weightMax.
     */
    public BigDecimal getWeightMax() {
        return weightMax;
    }

    /**
     * Set the weightMax.
     *
     * @param weightMax The weightMax to set.
     */
    public void setWeightMax(BigDecimal weightMax) {
        this.weightMax = weightMax;
    }

    /**
     * Returns the type.
     *
     * @return as String
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportUnitType that = (TransportUnitType) o;
        return Objects.equals(type, that.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    /**
     * {@code TransportUnitType} builder static inner class.
     */
    public static final class Builder {

        private String type;
        private String description;
        private int length;
        private int width;
        private int height;
        private BigDecimal weightTare;
        private BigDecimal weightMax;
        private BigDecimal payload;
        private String compatibility;
        private Set<TransportUnit> transportUnits;
        private Set<TypeStackingRule> typeStackingRules;
        private Set<TypePlacingRule> typePlacingRules;

        private Builder(String type) {
            this.type = type;
        }

        /**
         * Sets the {@code description} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code description} to set
         * @return a reference to this Builder
         */
        public Builder description(String val) {
            description = val;
            return this;
        }

        /**
         * Sets the {@code length} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code length} to set
         * @return a reference to this Builder
         */
        public Builder length(int val) {
            length = val;
            return this;
        }

        /**
         * Sets the {@code width} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code width} to set
         * @return a reference to this Builder
         */
        public Builder width(int val) {
            width = val;
            return this;
        }

        /**
         * Sets the {@code height} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code height} to set
         * @return a reference to this Builder
         */
        public Builder height(int val) {
            height = val;
            return this;
        }

        /**
         * Sets the {@code weightTare} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weightTare} to set
         * @return a reference to this Builder
         */
        public Builder weightTare(BigDecimal val) {
            weightTare = val;
            return this;
        }

        /**
         * Sets the {@code weightMax} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weightMax} to set
         * @return a reference to this Builder
         */
        public Builder weightMax(BigDecimal val) {
            weightMax = val;
            return this;
        }

        /**
         * Sets the {@code payload} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code payload} to set
         * @return a reference to this Builder
         */
        public Builder payload(BigDecimal val) {
            payload = val;
            return this;
        }

        /**
         * Sets the {@code compatibility} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code compatibility} to set
         * @return a reference to this Builder
         */
        public Builder compatibility(String val) {
            compatibility = val;
            return this;
        }

        /**
         * Sets the {@code transportUnits} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code transportUnits} to set
         * @return a reference to this Builder
         */
        public Builder transportUnits(Set<TransportUnit> val) {
            transportUnits = val;
            return this;
        }

        /**
         * Sets the {@code typeStackingRules} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code typeStackingRules} to set
         * @return a reference to this Builder
         */
        public Builder typeStackingRules(Set<TypeStackingRule> val) {
            typeStackingRules = val;
            return this;
        }

        /**
         * Sets the {@code typePlacingRules} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code typePlacingRules} to set
         * @return a reference to this Builder
         */
        public Builder typePlacingRules(Set<TypePlacingRule> val) {
            typePlacingRules = val;
            return this;
        }

        /**
         * Returns a {@code TransportUnitType} built from the parameters previously set.
         *
         * @return a {@code TransportUnitType} built with parameters of this {@code TransportUnitType.Builder}
         */
        public TransportUnitType build() {
            return new TransportUnitType(this);
        }
    }
}
