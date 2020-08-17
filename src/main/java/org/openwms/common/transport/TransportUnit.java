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
import org.ameba.integration.jpa.BaseEntity;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.openwms.common.location.Location;
import org.openwms.common.transport.barcode.Barcode;
import org.openwms.core.units.api.Weight;
import org.openwms.core.values.CoreTypeDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * A TransportUnit is an item like a box, a toad, a bin or a pallet that is moved within a warehouse and can carry goods. Used as container
 * to transport items like {@code LoadUnit}s. It can be moved between {@code Location}s.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 */
@Configurable
@Audited(targetAuditMode = NOT_AUDITED)
@AuditOverride(forClass = ApplicationEntity.class)
@AuditOverride(forClass = BaseEntity.class)
@Entity
@Table(name = "COM_TRANSPORT_UNIT", uniqueConstraints = @UniqueConstraint(columnNames = {"C_BARCODE"}))
public class TransportUnit extends ApplicationEntity implements Serializable {

    /** Unique natural key. */
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "C_BARCODE", length = Barcode.BARCODE_LENGTH, nullable = false))
    @OrderBy
    private Barcode barcode;

    /** Indicates whether the {@code TransportUnit} is empty or not (nullable). */
    @Column(name = "C_EMPTY")
    private Boolean empty;

    /** Date when the {@code TransportUnit} has been moved to the current {@link Location}. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "C_ACTUAL_LOCATION_DATE")
    private Date actualLocationDate;

    /** Weight of the {@code TransportUnit}. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unitType", column = @Column(name = "C_WEIGHT_UOM", length = CoreTypeDefinitions.QUANTITY_LENGTH)),
            @AttributeOverride(name = "magnitude", column = @Column(name = "C_WEIGHT"))
    })
    private Weight weight = Weight.ZERO;

    /** State of the {@code TransportUnit}. */
    @Column(name = "C_STATE")
    @Enumerated(EnumType.STRING)
    private TransportUnitState state = TransportUnitState.AVAILABLE;

    /** The current {@link Location} of the {@code TransportUnit}. */
    @ManyToOne
    @JoinColumn(name = "C_ACTUAL_LOCATION", nullable = false)
    private Location actualLocation;

    /** The target {@link Location} of the {@code TransportUnit}. This property will be set when a {@code TransportOrder} is started. */
    @ManyToOne
    @JoinColumn(name = "C_TARGET_LOCATION")
    private Location targetLocation;

    /** The {@link TransportUnitType} of the {@code TransportUnit}. */
    @ManyToOne
    @JoinColumn(name = "C_TRANSPORT_UNIT_TYPE", nullable = false)
    private TransportUnitType transportUnitType;

    /** Owning {@code TransportUnit}. */
    @ManyToOne
    @JoinColumn(name = "C_PARENT")
    private TransportUnit parent;

    /** The {@code User} who performed the last inventory action on the {@code TransportUnit}. */
    @Column(name = "C_INVENTORY_USER")
    private String inventoryUser;

    /** Date of last inventory check. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "C_INVENTORY_DATE")
    private Date inventoryDate;

    /** A set of all child {@code TransportUnit}s, ordered by id. */
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("id DESC")
    private Set<TransportUnit> children = new HashSet<>();

    /** A List of errors occurred on the {@code TransportUnit}. */
    @OneToMany(mappedBy = "tu", cascade = {CascadeType.ALL})
    @NotAudited
    private List<UnitError> errors = new ArrayList<>(0);

    @Autowired
    @Transient
    private transient EntityManager em;

    /*~ ----------------------------- constructors ------------------- */

    /** Dear JPA... */
    protected TransportUnit() {
    }

    /**
     * Create a new {@code TransportUnit} with an unique {@link Barcode}.
     *
     * @param barcode The unique identifier of this {@code TransportUnit} is the {@link Barcode} - must not be {@literal null}
     * @param tut The {@code TransportUnitType} of this {@code TransportUnit} - must not be {@literal null}
     * @param actualLocation The current {@code Location} of this {@code TransportUnit} - must not be {@literal null}
     * @throws IllegalArgumentException when one of the params is {@literal null}
     */
    public TransportUnit(@NotNull Barcode barcode, @NotNull TransportUnitType tut, @NotNull Location actualLocation) {
        Assert.notNull(barcode, "Barcode must not be null");
        Assert.notNull(tut, "TransportUnitType must not be null");
        Assert.notNull(actualLocation, "ActualLocation must not be null");
        this.barcode = barcode;
        setTransportUnitType(tut);
        setActualLocation(actualLocation);
    }
    /*~ ----------------------------- methods ------------------- */

    /**
     * Get the actual {@link Location} of the {@code TransportUnit}.
     *
     * @return The {@link Location} where the {@code TransportUnit} is placed on
     */
    public Location getActualLocation() {
        return actualLocation;
    }

    /**
     * Put the {@code TransportUnit} on a {@link Location}.
     *
     * @param actualLocation The new {@link Location} of the {@code TransportUnit}
     * @throws IllegalArgumentException when {@code actualLocation} is {@literal null}
     */
    public void setActualLocation(Location actualLocation) {
        Assert.notNull(actualLocation, () -> "ActualLocation must not be null, this: " + this);
        this.actualLocation = actualLocation;
        this.actualLocationDate = new Date();
        this.actualLocation.setLastMovement(this.actualLocationDate);
    }

    /**
     * Get the target {@link Location} of the {@code TransportUnit}. This property can not be {@literal null} when an active {@code
     * TransportOrder} exists.
     *
     * @return The target location
     */
    public Location getTargetLocation() {
        return this.targetLocation;
    }

    /**
     * Set the target {@link Location} of the {@code TransportUnit}. Shall only be set in combination with an active {@code
     * TransportOrder}.
     *
     * @param targetLocation The target {@link Location} where this {@code TransportUnit} shall be transported to
     */
    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    /**
     * Indicates whether the {@code TransportUnit} is empty or not.
     *
     * @return {@literal true} if empty, {@literal false} if not empty, {@literal null} when not defined
     */
    public Boolean getEmpty() {
        return this.empty;
    }

    /**
     * Marks the {@code TransportUnit} to be empty.
     *
     * @param empty {@literal true} to mark the {@code TransportUnit} as empty, {@literal false} to mark it as not empty and {@literal null}
     * for no definition
     */
    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    /**
     * Returns the username of the User who performed the last inventory action on the {@code TransportUnit}.
     *
     * @return The username who did the last inventory check
     */
    public String getInventoryUser() {
        return this.inventoryUser;
    }

    /**
     * Set the username who performed the last inventory action on the {@code TransportUnit}.
     *
     * @param inventoryUser The username who did the last inventory check
     */
    public void setInventoryUser(String inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    /**
     * Number of {@code TransportUnit}s belonging to the {@code TransportUnit}.
     *
     * @return The number of all {@code TransportUnit}s belonging to this one
     */
    public int getNoTransportUnits() {
        return this.children.size();
    }

    /**
     * Returns the date when the {@code TransportUnit} moved to the actualLocation.
     *
     * @return The timestamp when the {@code TransportUnit} moved the last time
     */
    public Date getActualLocationDate() {
        if (this.actualLocationDate == null) {
            return null;
        }
        return new Date(this.actualLocationDate.getTime());
    }

    /**
     * Returns the timestamp of the last inventory check of the {@code TransportUnit}.
     *
     * @return The timestamp of the last inventory check of the {@code TransportUnit}.
     */
    public Date getInventoryDate() {
        if (this.inventoryDate == null) {
            return null;
        }
        return new Date(this.inventoryDate.getTime());
    }

    /**
     * Set the timestamp of the last inventory action of the {@code TransportUnit}.
     *
     * @param inventoryDate The timestamp of the last inventory check
     * @throws IllegalArgumentException when {@code inventoryDate} is {@literal null}
     */
    public void setInventoryDate(Date inventoryDate) {
        Assert.notNull(inventoryDate, () -> "InventoryDate must not be null, this: " + this);
        this.inventoryDate = new Date(inventoryDate.getTime());
    }

    /**
     * Returns the current weight of the {@code TransportUnit}.
     *
     * @return The current weight of the {@code TransportUnit}
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Sets the current weight of the {@code TransportUnit}.
     *
     * @param weight The current weight of the {@code TransportUnit}
     */
    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public List<UnitError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    /**
     * Add an error to the {@code TransportUnit}.
     *
     * @param error An {@link UnitError} to be added
     * @return The key.
     * @throws IllegalArgumentException when {@code error} is {@literal null}
     */
    public UnitError addError(UnitError error) {
        Assert.notNull(error, () -> "Error must not be null, this: " + this);
        error.setTu(this);
        if (em != null && em.contains(this)) {
            // if the instance is currently bound to a PC...
            em.persist(error);
            error = em.merge(error);
        }
        this.errors.add(error);
        return error;
    }

    /**
     * Return the state of the {@code TransportUnit}.
     *
     * @return The current state of the {@code TransportUnit}
     */
    public TransportUnitState getState() {
        return this.state;
    }

    /**
     * Set the state of the {@code TransportUnit}.
     *
     * @param state The state to set on the {@code TransportUnit}
     */
    public void setState(TransportUnitState state) {
        this.state = state;
    }

    /**
     * Return the {@link TransportUnitType} of the {@code TransportUnit}.
     *
     * @return The {@link TransportUnitType} the {@code TransportUnit} belongs to
     */
    public TransportUnitType getTransportUnitType() {
        return this.transportUnitType;
    }

    /**
     * Set the {@link TransportUnitType} of the {@code TransportUnit}.
     *
     * @param transportUnitType The type of the {@code TransportUnit}
     */
    public void setTransportUnitType(TransportUnitType transportUnitType) {
        Assert.notNull(transportUnitType, () -> "TransportUnitType must not be null, this: " + this);
        this.transportUnitType = transportUnitType;
    }

    /**
     * Return the {@link Barcode} of the {@code TransportUnit}.
     *
     * @return The current {@link Barcode}
     */
    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * Returns the parent {@code TransportUnit}.
     *
     * @return the parent.
     */
    public TransportUnit getParent() {
        return parent;
    }

    /**
     * Set a parent {@code TransportUnit}.
     *
     * @param parent The parent to set.
     */
    public void setParent(TransportUnit parent) {
        this.parent = parent;
    }

    /**
     * Get all child {@code TransportUnit}s.
     *
     * @return the transportUnits.
     */
    public Set<TransportUnit> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Add a {@code TransportUnit} to the children.
     *
     * @param transportUnit The {@code TransportUnit} to be added to the list of children
     * @throws IllegalArgumentException when transportUnit is {@literal null}
     */
    public void addChild(TransportUnit transportUnit) {
        Assert.notNull(transportUnit, () -> "TransportUnitType must not be null, this: " + this);
        if (transportUnit.hasParent()) {
            if (transportUnit.getParent().equals(this)) {

                // if this instance is already the parent, we just return
                return;
            }

            // disconnect post from it's current relationship
            transportUnit.getParent().removeChild(transportUnit);
        }

        // make this instance the new parent
        transportUnit.setParent(this);
        this.children.add(transportUnit);
    }

    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Remove a {@code TransportUnit} from the list of children.
     *
     * @param transportUnit The {@code TransportUnit} to be removed from the list of children
     * @throws IllegalArgumentException when {@code transportUnit} is {@literal null} or not a child of this instance
     */
    public void removeChild(TransportUnit transportUnit) {
        Assert.notNull(transportUnit, () -> "TransportUnit must not be null, this: " + this);
        // make sure this is the parent before we break the relationship
        if (transportUnit.parent == null || !transportUnit.parent.equals(this)) {
            throw new IllegalArgumentException("Child TransportUnit not associated with this instance, this: " + this);
        }
        transportUnit.setParent(null);
        this.children.remove(transportUnit);
    }

    /**
     * {@inheritDoc}
     *
     * Return the {@link Barcode} as String.
     */
    @Override
    public String toString() {
        return barcode.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Uses barcode for comparison.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportUnit that = (TransportUnit) o;
        return barcode.equals(that.barcode);
    }

    /**
     * {@inheritDoc}
     *
     * Uses barcode for calculation.
     */
    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }
}
