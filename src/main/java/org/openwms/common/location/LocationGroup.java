/*
 * Copyright 2005-2023 the original author or authors.
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

import org.ameba.exception.ServiceLayerException;
import org.openwms.common.StateChangeException;
import org.openwms.common.account.Account;
import org.openwms.common.location.api.LocationGroupMode;
import org.openwms.common.location.api.LocationGroupState;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A LocationGroup is a logical group of {@code Location}s with same characteristics.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 * @see org.openwms.common.location.Location
 */
@Entity
@Table(name = "COM_LOCATION_GROUP", uniqueConstraints =
    @UniqueConstraint(name = "UC_LG_NAME", columnNames = "C_NAME")
)
public class LocationGroup extends Target implements Serializable {

    /** Unique identifier of a {@code LocationGroup}. */
    @Column(name = "C_NAME", nullable = false, length = LENGTH_NAME)
    @NotBlank
    @Size(min = 1, max = LENGTH_NAME)
    private String name;
    /** Length of the name field; used for telegram mapping and for column definition. */
    public static final int LENGTH_NAME = 255;

    /** The LocationGroup might be assigned to an {@link Account}. */
    @ManyToOne
    @JoinColumn(name = "C_ACCOUNT", referencedColumnName = "C_IDENTIFIER", foreignKey = @ForeignKey(name = "FK_LG_ACC"))
    private Account account;

    /** Description for the {@code LocationGroup}. */
    @Column(name = "C_DESCRIPTION")
    private String description;

    /** A type can be assigned to a {@code LocationGroup}. */
    @Column(name = "C_GROUP_TYPE")
    private String groupType;

    /** Is the {@code LocationGroup} included in the calculation of {@code TransportUnit}s. */
    @Column(name = "C_GROUP_COUNTING_ACTIVE")
    private boolean locationGroupCountingActive = true;

    /** The operation mode is controlled by the subsystem and defines the physical mode a LocationGroup is currently able to operate in. */
    @Column(name = "C_OP_MODE")
    @NotBlank
    private String operationMode = LocationGroupMode.INFEED_AND_OUTFEED;

    /** State of infeed, controlled by the subsystem only. */
    @Column(name = "C_GROUP_STATE_IN")
    @Enumerated(EnumType.STRING)
    @NotNull
    private LocationGroupState groupStateIn = LocationGroupState.AVAILABLE;

    /** References the {@code LocationGroup} that locked this {@code LocationGroup} for infeed. */
    @ManyToOne
    @JoinColumn(name = "C_IN_LOCKER", foreignKey = @ForeignKey(name = "FK_LG_LG_INLOCKER"))
    private LocationGroup stateInLocker;

    /** State of outfeed. */
    @Column(name = "C_GROUP_STATE_OUT")
    @Enumerated(EnumType.STRING)
    @NotNull
    private LocationGroupState groupStateOut = LocationGroupState.AVAILABLE;

    /** References the {@code LocationGroup} that locked this {@code LocationGroup} for outfeed. */
    @ManyToOne
    @JoinColumn(name = "C_OUT_LOCKER", foreignKey = @ForeignKey(name = "FK_LG_LG_OUTLOCKER"))
    private LocationGroup stateOutLocker;

    /** Maximum fill level of the {@code LocationGroup}. */
    @Column(name = "C_MAX_FILL_LEVEL")
    private float maxFillLevel = 0;

    /** The subsystem like a PLC, that manages this {@code LocationGroup}. */
    @Embedded
    private Subsystem subsystem;

    /* ------------------- collection mapping ------------------- */
    /** Parent {@code LocationGroup}. */
    @ManyToOne
    @JoinColumn(name = "C_PARENT", foreignKey = @ForeignKey(name = "FK_LG_LG_PARENT"))
    private LocationGroup parent;

    /** Child {@code LocationGroup}s. */
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL})
    private Set<LocationGroup> locationGroups = new HashSet<>();

    /** Child {@link Location}s. */
    @OneToMany(mappedBy = "locationGroup")
    private Set<Location> locations = new HashSet<>();

    /*~ ----------------------------- constructors ------------------- */
    /** Dear JPA... */
    protected LocationGroup() { }

    /**
     * Create a new {@code LocationGroup} with an unique name.
     *
     * @param name The name of the {@code LocationGroup} must not be {@literal null}
     */
    public LocationGroup(@NotBlank String name) {
        Assert.hasText(name, "Creation of LocationGroup with name null");
        this.name = name;
    }

    /*~ ----------------------------- methods ------------------- */

    /**
     * Returns the name of the {@code LocationGroup}.
     *
     * @return The name of the {@code LocationGroup}
     */
    public String getName() {
        return name;
    }

    /**
     * Return the {@link Account} this {@code LocationGroup} is assigned to.
     *
     * @return The Account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Check whether infeed is allowed for the {@code LocationGroup}.
     *
     * @return {@literal true} if allowed, otherwise {@literal false}.
     */
    public boolean isInfeedAllowed() {
        return (getGroupStateIn() == LocationGroupState.AVAILABLE);
    }

    /**
     * Check whether infeed of the {@code LocationGroup} is blocked.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}.
     */
    public boolean isInfeedBlocked() {
        return !isInfeedAllowed();
    }

    /**
     * Check whether outfeed is allowed for the {@code LocationGroup}.
     *
     * @return {@literal true} if allowed, otherwise {@literal false}.
     */
    public boolean isOutfeedAllowed() {
        return (getGroupStateOut() == LocationGroupState.AVAILABLE);
    }

    /**
     * Check whether outfeed of the {@code LocationGroup} is blocked.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}.
     */
    public boolean isOutfeedBlocked() {
        return !isOutfeedAllowed();
    }

    /**
     * Get the current operation mode this LocationGroup operates in.
     *
     * @return The operational mode
     */
    public String getOperationMode() {
        return operationMode;
    }

    /**
     * Set the current operation mode this LocationGroup can operate in.
     *
     * @param operationMode The mode as an extensible String
     * @see LocationGroupMode
     */
    public void setOperationMode(@NotBlank String operationMode) {
        this.operationMode = operationMode;
        this.locationGroups.forEach(lg -> lg.setOperationMode(operationMode));
    }

    /**
     * Returns the infeed state of the {@code LocationGroup}.
     *
     * @return The state of infeed
     */
    public LocationGroupState getGroupStateIn() {
        return this.groupStateIn;
    }

    /**
     * Change the infeed state of the {@code LocationGroup}.
     *
     * @param newGroupStateIn The state to set
     */
    public void changeGroupStateIn(LocationGroupState newGroupStateIn) {
        if (stateInLocker != null && stateInLocker != this) {
            throw new StateChangeException("The LocationGroup's state is blocked by any other LocationGroup and cannot be changed");
        }
        groupStateIn = newGroupStateIn;
        locationGroups.forEach(lg -> lg.changeGroupStateIn(newGroupStateIn, this));
    }

    /**
     * Change the infeed state of the {@code LocationGroup}.
     *
     * @param newGroupStateIn The state to set
     * @param lockLG The {@code LocationGroup} that wants to lock/unlock this {@code LocationGroup}.
     */
    private void changeGroupStateIn(LocationGroupState newGroupStateIn, LocationGroup lockLG) {
        if (groupStateIn == LocationGroupState.NOT_AVAILABLE && newGroupStateIn == LocationGroupState.AVAILABLE) {

            // unlock
            stateInLocker = null;
        }
        if (groupStateIn == LocationGroupState.AVAILABLE && newGroupStateIn == LocationGroupState.NOT_AVAILABLE) {

            // lock
            stateInLocker = lockLG;
        }
        groupStateIn = newGroupStateIn;
        locationGroups.forEach(lg -> lg.changeGroupStateIn(newGroupStateIn, lockLG));
    }

    /**
     * Return the outfeed state of the {@code LocationGroup}.
     *
     * @return The state of outfeed
     */
    public LocationGroupState getGroupStateOut() {
        return groupStateOut;
    }

    /**
     * Change the outfeed state of the {@code LocationGroup}.
     *
     * @param newGroupStateOut The state to set
     */
    public void changeGroupStateOut(LocationGroupState newGroupStateOut) {
        if (stateOutLocker != null && stateOutLocker != this) {
            throw new StateChangeException("The LocationGroup's state is blocked by any other LocationGroup and cannot be changed");
        }
        groupStateOut = newGroupStateOut;
        locationGroups.forEach(lg -> lg.changeGroupStateOut(newGroupStateOut, this));
    }

    /**
     * Set the outfeed state of the {@code LocationGroup}.
     *
     * @param gStateOut The state to set
     * @param lockLg The {@code LocationGroup} that wants to lock/unlock this {@code LocationGroup}.
     */
    void changeGroupStateOut(LocationGroupState gStateOut, LocationGroup lockLg) {
        if (this.groupStateOut == LocationGroupState.NOT_AVAILABLE && gStateOut == LocationGroupState.AVAILABLE && (this.stateOutLocker == null || this.stateOutLocker.equals(lockLg))) {
            this.groupStateOut = gStateOut;
            this.stateOutLocker = null;
            for (LocationGroup child : locationGroups) {
                child.changeGroupStateOut(gStateOut, lockLg);
            }
        }
        if (this.groupStateOut == LocationGroupState.AVAILABLE && gStateOut == LocationGroupState.NOT_AVAILABLE && (this.stateOutLocker == null || this.stateOutLocker.equals(lockLg))) {
            this.groupStateOut = gStateOut;
            this.stateOutLocker = lockLg;
            for (LocationGroup child : locationGroups) {
                child.changeGroupStateOut(gStateOut, lockLg);
            }
        }
    }

    /**
     * Returns the count of all sub {@link Location}s.
     *
     * @return The count of {@link Location}s belonging to this {@code LocationGroup}
     */
    public int getNoLocations() {
        return this.locations != null ? this.locations.size() : 0;
    }

    /**
     * Returns the maximum fill level of the {@code LocationGroup}.<br> The maximum fill level defines how many {@link Location}s of the
     * {@code LocationGroup} can be occupied by {@code TransportUnit}s. <p> The maximum fill level is a value between 0 and 1 and represents
     * a percentage value. </p>
     *
     * @return The maximum fill level
     */
    public float getMaxFillLevel() {
        return this.maxFillLevel;
    }

    /**
     * Set the maximum fill level for the {@code LocationGroup}. <p> Pass a value between 0 and 1.<br> For example maxFillLevel = 0.85
     * means: 85% of all {@link Location}s can be occupied. </p>
     *
     * @param maxFillLevel The maximum fill level
     */
    public void setMaxFillLevel(float maxFillLevel) {
        this.maxFillLevel = maxFillLevel;
    }

    /**
     * Returns the type of the {@code LocationGroup}.
     *
     * @return The type of the {@code LocationGroup}
     */
    public String getGroupType() {
        return this.groupType;
    }

    /**
     * Set the type for the {@code LocationGroup}.
     *
     * @param groupType The type of the {@code LocationGroup}
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * Returns the description text.
     *
     * @return The Description as String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description text.
     *
     * @param description The String to set as description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the parent {@code LocationGroup}.
     *
     * @return The parent {@code LocationGroup}
     */
    public LocationGroup getParent() {
        return this.parent;
    }

    /**
     * Return all child {@code LocationGroup}.
     *
     * @return A set of all {@code LocationGroup} having this one as parent
     */
    public Set<LocationGroup> getLocationGroups() {
        return locationGroups;
    }

    /**
     * Add a {@code LocationGroup} to the list of children.
     *
     * @param locationGroup The {@code LocationGroup} to be added as a child
     * @return {@literal true} if the {@code LocationGroup} was new in the collection of {@code LocationGroup}s, otherwise {@literal false}
     */
    public boolean addLocationGroup(LocationGroup locationGroup) {
        if (locationGroup == null) {
            throw new IllegalArgumentException("LocationGroup to be added is null");
        }
        if (locationGroup.parent != null) {
            locationGroup.parent.removeLocationGroup(locationGroup);
        }
        locationGroup.parent = this;
        locationGroup.changeGroupStateIn(groupStateIn, this);
        locationGroup.changeGroupStateOut(groupStateOut, this);
        return locationGroups.add(locationGroup);
    }

    /**
     * Remove a {@code LocationGroup} from the list of children.
     *
     * @param locationGroup The {@code LocationGroup} to be removed from the list of children
     * @return {@literal true} if the {@code LocationGroup} was found and could be removed, otherwise {@literal false}
     */
    public boolean removeLocationGroup(@NotNull LocationGroup locationGroup) {
        Assert.notNull(locationGroup, () -> "LocationGroup to remove is null. this: " + this);
        locationGroup.parent = null;
        return locationGroups.remove(locationGroup);
    }

    /**
     * Return all {@link Location}s.
     *
     * @return {@link Location}s
     */
    public Set<Location> getLocations() {
        return locations;
    }

    /**
     * Check whether this {@code LocationGroup} has {@code Location}s assigned.
     *
     * @return {@literal true} if {@code Location}s are assigned, otherwise {@literal false}
     */
    public boolean hasLocations() {
        return locations != null && !locations.isEmpty();
    }

    /**
     * Add a {@link Location} to the list of children.
     *
     * @param location The {@link Location} to be added as child
     * @return {@literal true} if the {@link Location} was new in the collection of {@link Location}s, otherwise {@literal false}
     */
    public boolean addLocation(Location location) {
        Assert.notNull(location, () -> "Location to be added to LocationGroup is null. this: " + this);
        location.setLocationGroup(this);
        return locations.add(location);
    }

    /**
     * Remove a {@link Location} from the list of children.
     *
     * @param location The {@link Location} to be removed from the list of children
     * @return {@literal true} if the {@link Location} was found and could be removed, otherwise {@literal false}
     */
    public boolean removeLocation(Location location) {
        Assert.notNull(location, () -> "Location to remove from LocationGroup is null. this: " + this);
        location.unsetLocationGroup();
        return locations.remove(location);
    }

    /**
     * Returns the locationGroupCountingActive.
     *
     * @return The locationGroupCountingActive
     */
    public boolean isLocationGroupCountingActive() {
        return locationGroupCountingActive;
    }

    /**
     * Set the locationGroupCountingActive.
     *
     * @param locationGroupCountingActive The locationGroupCountingActive to set
     */
    public void setLocationGroupCountingActive(boolean locationGroupCountingActive) {
        this.locationGroupCountingActive = locationGroupCountingActive;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 111;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocationGroup)) {
            return false;
        }
        LocationGroup other = (LocationGroup) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * Return the name of the {@code LocationGroup} as String.
     *
     * @return The name
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Tries to change the {@code groupStateIn} and {@code groupStateOut} of the {@code LocationGroup}. A state change is only allowed when
     * the parent {@code LocationGroup}s state is not blocked.
     *
     * @param stateIn The new groupStateIn to set, or {@literal null}
     * @param stateOut The new groupStateOut to set, or {@literal null}
     */
    public void changeState(LocationGroupState stateIn, LocationGroupState stateOut) {
        if (groupStateIn != stateIn && stateIn != null) {
            // GroupStateIn changed
            if (parent != null && parent.getGroupStateIn() == LocationGroupState.NOT_AVAILABLE && groupStateIn == LocationGroupState.AVAILABLE) {
                throw new ServiceLayerException("Not allowed to change GroupStateIn, parent locationGroup is not available");
            }
            changeGroupStateIn(stateIn, this);
        }
        if (groupStateOut != stateOut && stateOut != null) {
            // GroupStateOut changed
            if (parent != null && parent.getGroupStateOut() == LocationGroupState.NOT_AVAILABLE && groupStateOut == LocationGroupState.AVAILABLE) {
                throw new ServiceLayerException("Not allowed to change GroupStateOut, parent locationGroup is not available");
            }
            changeGroupStateOut(stateOut, this);
        }
    }

    /**
     * Whether this LocationGroup has a parent LocationGroup or not.
     *
     * @return {@literal true} If it has a parent
     */
    public boolean hasParent() {
        return parent != null;
    }
}