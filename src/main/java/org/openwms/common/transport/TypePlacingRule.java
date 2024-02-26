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
package org.openwms.common.transport;

import org.ameba.integration.jpa.BaseEntity;
import org.openwms.common.location.LocationType;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TypePlacingRule is a {@code Rule} that defines which types of {@code TransportUnit}s ({@code TransportUnitType}s) can be put on which
 * type of {@code Location} ({@code LocationType}). <p> A privilegeLevel is defined to order a list of allowed {@code LocationType}s. </p>
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 * @see TransportUnitType
 */
@Entity
@Table(name = "COM_TYPE_PLACING_RULE", uniqueConstraints = @UniqueConstraint(columnNames = {"C_TUT_ID",
        "C_PRIVILEGE_LEVEL", "C_ALLOWED_LOCATION_TYPE"}))
public class TypePlacingRule extends BaseEntity implements Serializable, Rule {

    /** To separate fields in toString method. */
    static final String SEPARATOR = "::";
    /** Parent {@link TransportUnitType} (not-null). */
    @ManyToOne
    @JoinColumn(name = "C_TUT_ID", nullable = false)
    private TransportUnitType transportUnitType;

    /**
     * The privilegeLevel defines a priority to describe which {@link TransportUnitType} can be placed on which {@link LocationType}. <p> A
     * value of 0 means the lowest priority. Increasing the privilegeLevel implies a higher priority and means the {@link TransportUnitType}
     * can be placed to the {@link LocationType} with the highest privilegeLevel. </p> <p> To forbid a {@link TransportUnitType} on a
     * certain {@link LocationType} the privilegeLevel must be set to -1. </p> (not-null)
     */
    @Column(name = "C_PRIVILEGE_LEVEL", nullable = false)
    private int privilegeLevel = DEF_PRIVILEGE_LEVEL;
    /** Default value of {@link #privilegeLevel}. */
    static final int DEF_PRIVILEGE_LEVEL = 0;

    /** An allowed {@link LocationType} on which the owning {@link TransportUnitType} may be placed (not-null). */
    @ManyToOne
    @JoinColumn(name = "C_ALLOWED_LOCATION_TYPE", nullable = false)
    private LocationType allowedLocationType;

    /*~ ----------------------------- constructors ------------------- */

    /** Dear JPA... */
    protected TypePlacingRule() {
    }

    /**
     * Create a new {@code TypePlacingRule} with privilegeLevel and allowedLocationType.
     *
     * @param transportUnitType The {@link TransportUnitType} for this rule, may not be {@literal null}
     * @param allowedLocationType The allowed {@link LocationType}, may not be {@literal null}
     * @param privilegeLevel The privilege level
     */
    public TypePlacingRule(TransportUnitType transportUnitType, LocationType allowedLocationType, int privilegeLevel) {
        Assert.notNull(transportUnitType, "When constructing a TypePlacingRule the TransportUnitType may not be null");
        Assert.notNull(allowedLocationType, "When constructing a TypePlacingRule the LocationType may not be null");
        this.transportUnitType = transportUnitType;
        this.allowedLocationType = allowedLocationType;
        this.privilegeLevel = privilegeLevel;
    }

    /**
     * Create a new {@code TypePlacingRule} with allowedLocationType.
     *
     * @param transportUnitType The {@link TransportUnitType} for this rule
     * @param allowedLocationType The allowed {@link LocationType}
     */
    public TypePlacingRule(TransportUnitType transportUnitType, LocationType allowedLocationType) {
        this(transportUnitType, allowedLocationType, DEF_PRIVILEGE_LEVEL);
    }

    /*~ ----------------------------- methods ------------------- */

    /**
     * Get the transportUnitType.
     *
     * @return The transportUnitType.
     */
    public TransportUnitType getTransportUnitType() {
        return transportUnitType;
    }

    /**
     * Get the privilegeLevel.
     *
     * @return The privilegeLevel.
     */
    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    /**
     * Get the allowedLocationType.
     *
     * @return The allowedLocationType.
     */
    public LocationType getAllowedLocationType() {
        return allowedLocationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePlacingRule that = (TypePlacingRule) o;
        return privilegeLevel == that.privilegeLevel &&
                Objects.equals(transportUnitType, that.transportUnitType) &&
                Objects.equals(allowedLocationType, that.allowedLocationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(transportUnitType, privilegeLevel, allowedLocationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return privilegeLevel + SEPARATOR + transportUnitType + SEPARATOR + allowedLocationType;
    }
}