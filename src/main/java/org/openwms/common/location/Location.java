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
package org.openwms.common.location;

import org.openwms.common.account.Account;
import org.springframework.util.Assert;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.openwms.common.location.StringListConverter.STRING_LIST_LENGTH;

/**
 * A Location, represents a physical or virtual place in a warehouse. Could be something like a storage location in the stock or a conveyor
 * location. Even error locations can be represented with the Location. Multiple Locations with same characteristics are grouped to a
 * {@link LocationGroup}.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 * @see org.openwms.common.location.LocationGroup
 */
@Entity
@Table(name = Location.TABLE, uniqueConstraints = @UniqueConstraint(name = "UC_LOC_ID", columnNames = {"C_AREA", "C_AISLE", "C_X", "C_Y", "C_Z"}))
public class Location extends Target implements Serializable {

    /** Table name. */
    public static final String TABLE = "COM_LOCATION";

    /** Unique natural key. */
    @Embedded
    @AttributeOverride(name = "area", column = @Column(name = "C_AREA"))
    @AttributeOverride(name = "aisle", column = @Column(name = "C_AISLE"))
    @AttributeOverride(name = "x", column = @Column(name = "C_X"))
    @AttributeOverride(name = "y", column = @Column(name = "C_Y"))
    @AttributeOverride(name = "z", column = @Column(name = "C_Z"))
    private LocationPK locationId;

    /** The Location might be assigned to an {@link Account}. */
    @ManyToOne
    @JoinColumn(name = "C_ACCOUNT", referencedColumnName = "C_IDENTIFIER", foreignKey = @ForeignKey(name = "FK_LOC_ACC"))
    private Account account;

    /** PLC code of the Location. */
    @Column(name = "C_PLC_CODE", unique = true)
    private String plcCode;

    /** ERP code of the Location. */
    @Column(name = "C_ERP_CODE", unique = true)
    private String erpCode;

    /** Description of the Location. */
    @Column(name = "C_DESCRIPTION")
    @Size(max = 255)
    private String description;

    /** Sort order index used by Putaway strategies. */
    @Column(name = "C_SORT")
    private Integer sortOrder;

    /** May be assigned to a particular zone in stock. */
    @Column(name = "C_STOCK_ZONE")
    private String stockZone;

    /** A {@code Location} can be assigned to a particular labels. */
    @Column(name="C_LABELS", length = STRING_LIST_LENGTH)
    @Size(max = STRING_LIST_LENGTH)
    @Convert(converter = StringListConverter.class)
    private List<String> labels;

    /** Maximum number of {@code TransportUnit}s allowed on this Location. */
    @Column(name = "C_NO_MAX_TRANSPORT_UNITS")
    private int noMaxTransportUnits = DEF_MAX_TU;
    /** Default value of {@link #noMaxTransportUnits}. */
    public static final int DEF_MAX_TU = 1;

    /** Maximum allowed weight on this Location. */
    @Column(name = "C_MAXIMUM_WEIGHT")
    private BigDecimal maximumWeight;

    /** Whether or not moving Products without {@code TransportUnit} to this Location is allowed. */
    @Column(name = "C_DIRECT_BOOKING_ALLOWED")
    private Boolean directBookingAllowed = true;

    /**
     * Date of last change. When a {@code TransportUnit} is moving to or away from this Location, {@code lastMovement} will be updated. This
     * is useful to get the history of {@code TransportUnit}s as well as for inventory calculation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "C_LAST_MOVEMENT")
    private Date lastMovement;

    /** Flag to indicate whether {@code TransportUnit}s should be counted on this Location or not. */
    @Column(name = "C_COUNTING_ACTIVE")
    private boolean countingActive = DEF_COUNTING_ACTIVE;
    /** Default value of {@link #countingActive}. */
    public static final boolean DEF_COUNTING_ACTIVE = false;

    /** Reserved for stock check procedure and inventory calculation. */
    @Column(name = "C_CHECK_STATE")
    private String checkState = DEF_CHECK_STATE;
    /** Default value of {@link #checkState}. */
    public static final String DEF_CHECK_STATE = "--";

    /**
     * Shall this Location be integrated in the calculation of {@code TransportUnit}s of the parent {@link LocationGroup}.<ul><li>{@literal
     * true} : Location is included in calculation of {@code TransportUnit}s.</li><li>{@literal false}: Location is not included in
     * calculation of {@code TransportUnit}s.</li></ul>
     */
    @Column(name = "C_LG_COUNTING_ACTIVE")
    private boolean locationGroupCountingActive = DEF_LG_COUNTING_ACTIVE;
    /** Default value of {@link #locationGroupCountingActive}. */
    public static final boolean DEF_LG_COUNTING_ACTIVE = false;

    /**
     * Signals the incoming state of this Location.
     * Locations which are blocked for incoming cannot pick up {@code TransportUnit}s.
     * <ul>
     * <li>{@literal true} : Location is ready to pick up {@code TransportUnit}s.</li>
     * <li>{@literal false}: Location is locked, and cannot pick up {@code TransportUnit}s.</li>
     * </ul>
     */
    @Column(name = "C_INCOMING_ACTIVE")
    private boolean incomingActive = DEF_INCOMING_ACTIVE;
    /** Default value of {@link #incomingActive}. */
    public static final boolean DEF_INCOMING_ACTIVE = true;

    /**
     * Signals the outgoing state of this Location.
     * Locations which are blocked for outgoing cannot release {@code TransportUnit}s.
     * <ul>
     * <li>{@literal true} : Location is enabled for outgoing {@code TransportUnit}s.</li>
     * <li>{@literal false}: Location is locked, {@code TransportUnit}s can't leave this Location.</li>
     * </ul>
     */
    @Column(name = "C_OUTGOING_ACTIVE")
    private boolean outgoingActive = DEF_OUTGOING_ACTIVE;
    /** Default value of {@link #outgoingActive}. */
    public static final boolean DEF_OUTGOING_ACTIVE = true;

    /**
     * The PLC is able to change the state of a Location. This property stores the last state, received from the PLC.
     * <ul>
     *     <li>0 : No PLC error, everything okay</li>
     *     <li><0: Not defined</li>
     *     <li>>0: Some kind of defined error code</li>
     * </ul>
     */
    @Column(name = "C_PLC_STATE")
    private int plcState = DEF_PLC_STATE;
    /** Default value of {@link #plcState}. */
    public static final int DEF_PLC_STATE = 0;

    /**
     * Determines whether the Location is considered in the allocation procedure.<ul><li>{@literal true} : This Location will be considered
     * in storage calculation by an allocation procedure.</li><li>{@literal false} : This Location will not be considered in the allocation
     * process.</li></ul>
     */
    @Column(name = "C_CONSIDERED_IN_ALLOCATION")
    private boolean consideredInAllocation = DEF_CONSIDERED_IN_ALLOCATION;
    /** Default value of {@link #consideredInAllocation}. */
    public static final boolean DEF_CONSIDERED_IN_ALLOCATION = true;

    /** The {@link LocationType} this Location belongs to. */
    @ManyToOne
    @JoinColumn(name = "C_LOCATION_TYPE", foreignKey = @ForeignKey(name = "FK_LOC_LT"))
    private LocationType locationType;

    /** Some group this Location belongs to. */
    @Column(name = "C_GROUP")
    private String group;

    /** The Location may be classified, like hazardeous. */
    @Column(name = "C_CLASSIFICATION")
    @Size(max = 255)
    private String classification;

    /** The {@link LocationGroup} this Location belongs to. */
    @ManyToOne
    @JoinColumn(name = "C_LOCATION_GROUP", foreignKey = @ForeignKey(name = "FK_LOC_LG"))
    private LocationGroup locationGroup;

    /** Stored {@link Message}s on this Location. */
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "COM_LOCATION_MESSAGE", joinColumns = @JoinColumn(name = "C_LOCATION_ID"), inverseJoinColumns = @JoinColumn(name = "C_MESSAGE_ID"))
    private Set<Message> messages = new HashSet<>();

    /*~ ----------------------------- constructors ------------------- */

    /**
     * Create a new Location with the business key.
     *
     * @param locationId The unique natural key of the Location
     */
    protected Location(LocationPK locationId) {
        Assert.notNull(locationId, "Creation of Location with locationId null");
        this.locationId = locationId;
    }

    /** Dear JPA... */
    protected Location() {
    }

    /**
     * Create a new Location with the business key.
     *
     * @param locationId The unique natural key of the Location
     * @return The Location
     */
    public static Location create(LocationPK locationId) {
        Assert.notNull(locationId, "Creation of Location with locationId null");
        return new Location(locationId);
    }
    /*~ ----------------------------- methods ------------------- */

    /**
     * Check if the Location has a {@code locationId} set.
     * @return {@literal true} if so
     */
    public boolean hasLocationId() {
        return locationId != null;
    }

    /**
     * Return the {@link Account} this {@code Location} is assigned to.
     *
     * @return The Account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Get the ERP Code of the Location.
     *
     * @return The ERP code
     */
    public String getErpCode() {
        return erpCode;
    }

    /**
     * Get the PLC Code of the Location.
     *
     * @return The PLC code
     */
    public String getPlcCode() {
        return plcCode;
    }

    /**
     * Add a new {@link Message} to this Location.
     *
     * @param message The {@link Message} to be added
     * @return {@literal true} if the {@link Message} is new in the collection of messages, otherwise {@literal false}
     */
    public boolean addMessage(Message message) {
        Assert.notNull(message, "null passed to addMessage, this: " + this);
        return this.messages.add(message);
    }

    /**
     * Returns the checkState to indicate the stock check procedure.
     *
     * @return The checkState
     */
    public String getCheckState() {
        return this.checkState;
    }

    /**
     * Determine whether the Location is considered during allocation.
     *
     * @return {@literal true} when considered in allocation, otherwise {@literal false}
     */
    public boolean isConsideredInAllocation() {
        return this.consideredInAllocation;
    }

    /**
     * Determine whether {@code TransportUnit}s should be counted on this Location or not.
     *
     * @return {@literal true} when counting is active, otherwise {@literal false}
     */
    public boolean isCountingActive() {
        return this.countingActive;
    }

    /**
     * Returns the description of the Location.
     *
     * @return The description text
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description text of the Location.
     *
     * @param description The description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the sortOrder.
     *
     * @return A sequence number
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * Returns the stockZone.
     *
     * @return As string
     */
    public String getStockZone() {
        return stockZone;
    }

    /**
     * Returns the list of Strings set as labels for the Location.
     *
     * @return A list of Strings or an empty list
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Set a list of labels to the Location.
     *
     * @param labels A comma-separated list of labels
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Determine whether incoming mode is activated and {@code TransportUnit}s can be put on this Location.
     *
     * @return {@literal true} when incoming mode is activated, otherwise {@literal false}
     */
    public boolean isInfeedActive() {
        return this.incomingActive;
    }

    /**
     * Set the incoming mode of this Location.
     *
     * @param infeedActive {@literal true} means Infeed movements are possible, {@literal false} means Infeed movements are blocked
     */
    public void setInfeed(boolean infeedActive) {
        this.incomingActive = infeedActive;
    }

    /**
     * Check whether infeed is blocked and moving {@code TransportUnit}s to here is forbidden.
     *
     * @return {@literal true} is blocked, otherwise {@literal false}
     */
    public boolean isInfeedBlocked() {
        return !this.incomingActive;
    }

    /**
     * Return the date when the Location was updated the last time.
     *
     * @return Timestamp of the last update
     */
    public Date getLastMovement() {
        return this.lastMovement;
    }

    /**
     * Change the date when a TransportUnit was put or left the Location the last time.
     *
     * @param lastMovement The date of change.
     */
    public void setLastMovement(Date lastMovement) {
        this.lastMovement = lastMovement;
    }

    /**
     * Return the {@link LocationGroup} where the Location belongs to.
     *
     * @return The {@link LocationGroup} of the Location
     */
    public LocationGroup getLocationGroup() {
        return this.locationGroup;
    }

    /**
     * Determine whether the Location is part of the parent {@link LocationGroup}s calculation procedure of {@code TransportUnit}s.
     *
     * @return {@literal true} if calculation is activated, otherwise {@literal false}
     */
    public boolean isLocationGroupCountingActive() {
        return this.locationGroupCountingActive;
    }

    /**
     * Returns the locationId (natural key) of the Location.
     *
     * @return The locationId
     */
    public LocationPK getLocationId() {
        return this.locationId;
    }

    /**
     * Returns the type of Location.
     *
     * @return The type
     */
    public LocationType getLocationType() {
        return this.locationType;
    }

    /**
     * Returns the group the Location belongs to.
     *
     * @return The group as String
     */
    public String getGroup() {
        return group;
    }

    /**
     * Returns the classification of the Location.
     *
     * @return As a String
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Set the classification.
     *
     * @param classification As an arbitrary String
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * Return the maximum allowed weight on the Location.
     *
     * @return The maximum allowed weight
     */
    public BigDecimal getMaximumWeight() {
        return this.maximumWeight;
    }

    /**
     * Whether or not moving Products without {@code TransportUnit} to this Location is allowed.
     *
     * @return {@literal true} if allowed, otherwise {@literal false}
     */
    public boolean isDirectBookingAllowed() {
        return directBookingAllowed;
    }

    /**
     * Returns an unmodifiable Set of {@link Message}s stored for the Location.
     *
     * @return An unmodifiable Set
     */
    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    /**
     * Returns the maximum number of {@code TransportUnit}s allowed on the Location.
     *
     * @return The maximum number of {@code TransportUnit}s
     */
    public int getNoMaxTransportUnits() {
        return noMaxTransportUnits;
    }

    /**
     * Determine whether outgoing mode is activated and {@code TransportUnit}s can leave this Location.
     *
     * @return {@literal true} when outgoing mode is activated, otherwise {@literal false}
     */
    public boolean isOutfeedActive() {
        return this.outgoingActive;
    }

    /**
     * Check whether outfeed is blocked and moving {@code TransportUnit}s from here is forbidden.
     *
     * @return {@literal true} is blocked, otherwise {@literal false}
     */
    public boolean isOutfeedBlocked() {
        return !this.outgoingActive;
    }

    /**
     * Set the outfeed mode of this Location.
     *
     * @param outfeedActive {@literal true} means Outfeed movements are possible, {@literal false} means Outfeed movements are blocked
     */
    public void setOutfeed(boolean outfeedActive) {
        this.outgoingActive = outfeedActive;
    }


    /**
     * Return the current set plc state.
     *
     * @return the plc state
     */
    public int getPlcState() {
        return plcState;
    }

    /**
     * Set the plc state.
     *
     * @param plcState the plc state
     */
    public void setPlcState(int plcState) {
        this.plcState = plcState;
    }

    /**
     * Remove one or more {@link Message}s from this Location.
     *
     * @param msgs An array of {@link Message}s to be removed
     * @return {@literal true} if the {@link Message}s were found and removed, otherwise {@literal false}
     * @throws IllegalArgumentException when messages is {@literal null}
     */
    public boolean removeMessages(Message... msgs) {
        Assert.notNull(msgs, () -> "null passed to removeMessages, this: " + this);
        return this.messages.removeAll(Arrays.asList(msgs));
    }

    /**
     * Add this {@code Location} to the {@literal locationGroup}. When the argument is {@literal null} an existing {@link LocationGroup} is
     * removed from the {@code Location}.
     *
     * @param locationGroup The {@link LocationGroup} to be assigned
     */
    void setLocationGroup(LocationGroup locationGroup) {
        Assert.notNull(locationGroup, () -> "Not allowed to call location#setLocationGroup with null argument, this: " + this);
        if (this.locationGroup != null) {
            this.locationGroup.removeLocation(this);
        }
        this.setLocationGroupCountingActive(locationGroup.isLocationGroupCountingActive());
        this.locationGroup = locationGroup;
    }

    /**
     * Define whether or not the Location shall be considered in counting {@code TransportUnit}s of the parent {@link LocationGroup}.
     *
     * @param locationGroupCountingActive {@literal true} if considered, otherwise {@literal false}
     */
    public void setLocationGroupCountingActive(boolean locationGroupCountingActive) {
        this.locationGroupCountingActive = locationGroupCountingActive;
    }

    /**
     * Checks whether this {@code Location} belongs to a {@code LocationGroup}.
     *
     * @return {@literal true} if it belongs to a {@code LocationGroup}, otherwise {@literal false}
     */
    public boolean belongsToLocationGroup() {
        return locationGroup != null;
    }

    /**
     * Checks whether this {@code Location} belongs NOT to a {@code LocationGroup}.
     *
     * @return {@literal true} if it does not belong to a {@code LocationGroup}, otherwise {@literal false}
     */
    public boolean belongsNotToLocationGroup() {
        return !belongsToLocationGroup();
    }

    /**
     * Set the locationGroup to {@literal null}.
     */
    void unsetLocationGroup() {
        this.locationGroup = null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only use the unique natural key for comparison.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(locationId, location.locationId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only use the unique natural key for hashCode calculation.
     */
    @Override
    public int hashCode() {
        return Objects.hash(locationId);
    }

    /**
     * Return the {@link LocationPK} as String.
     *
     * @return String locationId
     * @see LocationPK#toString()
     */
    @Override
    public String toString() {
        return locationId.toString();
    }
}
