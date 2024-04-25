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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.openwms.common.account.Account;
import org.openwms.common.app.Default;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;
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
@Table(name = Location.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = "UC_LOC_ID", columnNames = {"C_AREA", "C_AISLE", "C_X", "C_Y", "C_Z"}),
        @UniqueConstraint(name = "UC_LOC_PLC_CODE", columnNames = "C_PLC_CODE"),
        @UniqueConstraint(name = "UC_LOC_ERP_CODE", columnNames = "C_ERP_CODE")
})
public class Location extends Target implements Serializable {

    /** Table name. */
    public static final String TABLE = "COM_LOCATION";
    private static final String CREATION_OF_LOCATION_WITH_LOCATION_ID_NULL = "Creation of Location with locationId null";

    /** Unique natural key. */
    @Embedded
    @NotNull
    @AttributeOverride(name = "area", column = @Column(name = "C_AREA"))
    @AttributeOverride(name = "aisle", column = @Column(name = "C_AISLE"))
    @AttributeOverride(name = "x", column = @Column(name = "C_X"))
    @AttributeOverride(name = "y", column = @Column(name = "C_Y"))
    @AttributeOverride(name = "z", column = @Column(name = "C_Z"))
    private LocationPK locationId;

    /** The {@code Location} might be assigned to an {@link Account}. */
    @ManyToOne
    @JoinColumn(name = "C_ACCOUNT", referencedColumnName = "C_IDENTIFIER", foreignKey = @ForeignKey(name = "FK_LOC_ACC"))
    private Account account;

    /** PLC code of the {@code Location}. */
    @Column(name = "C_PLC_CODE")
    private String plcCode;

    /** ERP code of the {@code Location}. */
    @Column(name = "C_ERP_CODE", unique = true)
    private String erpCode;

    /** Description of the {@code Location}. */
    @Column(name = "C_DESCRIPTION")
    @Size(max = 255)
    private String description;

    /** Sort order index used by strategies for putaway, or picking. */
    @Column(name = "C_SORT")
    private Integer sortOrder;

    /** Might be assigned to a particular zone in stock. */
    @Column(name = "C_STOCK_ZONE")
    private String stockZone;

    /** A {@code Location} can be assigned to a particular labels. */
    @Column(name="C_LABELS", length = STRING_LIST_LENGTH)
    @Convert(converter = StringListConverter.class)
    @Size(max = STRING_LIST_LENGTH)
    private List<String> labels;

    /** Maximum number of {@code TransportUnit}s allowed on the {@code Location}. */
    @Column(name = "C_NO_MAX_TRANSPORT_UNITS")
    private int noMaxTransportUnits = DEF_MAX_TU;
    /** Default value of {@link #noMaxTransportUnits}. */
    public static final int DEF_MAX_TU = 1;

    /** Maximum allowed weight on the {@code Location}. */
    @Column(name = "C_MAXIMUM_WEIGHT")
    private BigDecimal maximumWeight;

    /**
     * Date of last movement. When a {@code TransportUnit} is moving to or away from the {@code Location}, {@code lastMovement} is updated.
     * This is useful to get the history of {@code TransportUnit}s as well as for inventory calculation.
     */
    @Column(name = "C_LAST_MOVEMENT")
    private LocalDateTime lastMovement;

    /**
     * Shall the {@code Location} be included in the calculation of {@code TransportUnit}s of the parent {@link LocationGroup}.
     * <ul>
     *     <li>{@literal true} : {@code Location} is included in calculation of {@code TransportUnit}s.</li>
     *     <li>{@literal false}: {@code Location} is not included in calculation of {@code TransportUnit}s.</li>
     * </ul>
     */
    @Column(name = "C_LG_COUNTING_ACTIVE")
    private Boolean locationGroupCountingActive = DEF_LG_COUNTING_ACTIVE;
    /** Default value of {@link #locationGroupCountingActive}. */
    public static final boolean DEF_LG_COUNTING_ACTIVE = false;

    /**
     * Signals the incoming state of the {@code Location}.
     * {@code Location}s which are blocked for incoming movements do not accept {@code TransportUnit}s.
     * <ul>
     *     <li>{@literal true} : {@code Location} is ready to pick up {@code TransportUnit}s.</li>
     *     <li>{@literal false}: {@code Location} is locked, and cannot pick up {@code TransportUnit}s.</li>
     * </ul>
     */
    @Column(name = "C_INCOMING_ACTIVE")
    private boolean incomingActive = DEF_INCOMING_ACTIVE;
    /** Default value of {@link #incomingActive}. */
    public static final boolean DEF_INCOMING_ACTIVE = true;

    /**
     * Signals the outgoing state of the {@code Location}.
     * {@code Location}s which are blocked for outgoing do not accept to move {@code TransportUnit}s away.
     * <ul>
     *     <li>{@literal true} : {@code Location} is enabled for outgoing {@code TransportUnit}s.</li>
     *     <li>{@literal false}: {@code Location} is locked, {@code TransportUnit}s can't leave the {@code Location}.</li>
     * </ul>
     */
    @Column(name = "C_OUTGOING_ACTIVE")
    private boolean outgoingActive = DEF_OUTGOING_ACTIVE;
    /** Default value of {@link #outgoingActive}. */
    public static final boolean DEF_OUTGOING_ACTIVE = true;

    /**
     * The PLC is able to change the state of a {@code Location}. This property stores the last state, received from the PLC.
     * <ul>
     *     <li>0 : No PLC error, everything okay</li>
     *     <li>&lt; 0: Not defined</li>
     *     <li>&gt; 0: Some defined error code</li>
     * </ul>
     */
    @Column(name = "C_PLC_STATE")
    private int plcState = DEF_PLC_STATE;
    /** Default value of {@link #plcState}. */
    public static final int DEF_PLC_STATE = 0;

    /**
     * Determines whether the {@code Location} is considered in the allocation procedure.
     * <ul>
     *     <li>{@literal true} : The {@code Location} is considered in storage calculation by an allocation procedure.</li>
     *     <li>{@literal false} : The {@code Location} is not considered in the allocation process.</li>
     * </ul>
     */
    @Column(name = "C_CONSIDERED_IN_ALLOCATION")
    private Boolean consideredInAllocation = DEF_CONSIDERED_IN_ALLOCATION;
    /** Default value of {@link #consideredInAllocation}. */
    public static final boolean DEF_CONSIDERED_IN_ALLOCATION = true;

    /** The {@link LocationType} the {@code Location} belongs to. */
    @ManyToOne
    @JoinColumn(name = "C_LOCATION_TYPE", foreignKey = @ForeignKey(name = "FK_LOC_LT"))
    private LocationType locationType;

    /** Some group the {@code Location} belongs to. */
    @Column(name = "C_GROUP")
    private String group;

    /** The {@code Location} may be classified, like 'hazardous'. */
    @Column(name = "C_CLASSIFICATION")
    @Size(max = 255)
    private String classification;

    /** The {@link LocationGroup} the {@code Location} belongs to. */
    @ManyToOne
    @JoinColumn(name = "C_LOCATION_GROUP", foreignKey = @ForeignKey(name = "FK_LOC_LG"))
    private LocationGroup locationGroup;

    /** Stored {@link Message}s on the {@code Location}. */
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "COM_LOCATION_MESSAGE",
            uniqueConstraints = @UniqueConstraint(name = "UC_LOCM_ID", columnNames = "C_MESSAGE_ID"),
            joinColumns = @JoinColumn(name = "C_LOCATION_ID", foreignKey = @ForeignKey(name = "FK_LOCM_LOCPK")),
            inverseJoinColumns = @JoinColumn(name = "C_MESSAGE_ID", foreignKey = @ForeignKey(name = "FK_LOCM_MSGPK"))
    )
    private Set<Message> messages = new HashSet<>();

    /*~ ----------------------------- constructors ------------------- */

    /**
     * Create a new Location with the business key.
     *
     * @param locationId The unique natural key of the Location
     */
    protected Location(LocationPK locationId) {
        Assert.notNull(locationId, CREATION_OF_LOCATION_WITH_LOCATION_ID_NULL);
        this.locationId = locationId;
    }

    /**
     * Create a new Location.
     *
     * @param locationId The unique natural key of the Location
     * @param locationGroup The LocationGroup the Location belongs to
     */
    @Default
    Location(LocationPK locationId, Account account, LocationGroup locationGroup, LocationType locationType, String erpCode,
            String plcCode, Integer sortOrder, String stockZone) {
        Assert.notNull(locationId, CREATION_OF_LOCATION_WITH_LOCATION_ID_NULL);
        this.locationId = locationId;
        this.account = account;
        this.locationGroup = locationGroup;
        this.locationType = locationType;
        this.erpCode = erpCode;
        this.plcCode = plcCode;
        this.sortOrder = sortOrder;
        this.stockZone = stockZone;
    }

    /** Dear JPA... */
    protected Location() { }

    /**
     * Create a new Location with the business key.
     *
     * @param locationId The unique natural key of the Location
     * @return The Location
     */
    public static Location create(LocationPK locationId) {
        return new Location(locationId);
    }
    /*~ ----------------------------- methods ------------------- */
    /** Required for the Mapper. */
    @Override
    public void setPersistentKey(String pKey) {
        super.setPersistentKey(pKey);
    }

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
     * Determine whether the Location is considered during allocation.
     *
     * @return {@literal true} when considered in allocation, otherwise {@literal false}
     */
    public boolean isConsideredInAllocation() {
        return this.consideredInAllocation;
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
    public LocalDateTime getLastMovement() {
        return this.lastMovement;
    }

    /**
     * Change the date when a TransportUnit was put or left the Location the last time.
     *
     * @param lastMovement The date of change.
     */
    public void setLastMovement(LocalDateTime lastMovement) {
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

    public void setLocationType(LocationType locationType) {
        if (this.locationType != null && !this.locationType.equals(locationType)) {
            throw new IllegalArgumentException(format("LocationType of Location [%s] is already defined and can't be changed", locationType));
        }
        this.locationType = locationType;
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
     * Returns an unmodifiable Set of {@link Message}s stored for the Location.
     *
     * @return An unmodifiable Set
     */
    public Set<Message> getMessages() {
        return new HashSet<>(messages);
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


    public static final class LocationBuilder {
        private final Location target;

        private LocationBuilder(Location target) {
            this.target = target;
        }

        public static LocationBuilder aLocation(Location target) {
            return new LocationBuilder(target);
        }

        public LocationBuilder withAccount(Account account) {
            this.target.account = account;
            return this;
        }

        public LocationBuilder withPlcCode(String plcCode) {
            this.target.plcCode = plcCode;
            return this;
        }

        public LocationBuilder withErpCode(String erpCode) {
            this.target.erpCode = erpCode;
            return this;
        }

        public LocationBuilder withDescription(String description) {
            this.target.description = description;
            return this;
        }

        public LocationBuilder withSortOrder(Integer sortOrder) {
            this.target.sortOrder = sortOrder;
            return this;
        }

        public LocationBuilder withStockZone(String stockZone) {
            this.target.stockZone = stockZone;
            return this;
        }

        public LocationBuilder withLabels(List<String> labels) {
            this.target.labels = labels;
            return this;
        }

        public LocationBuilder withNoMaxTransportUnits(int noMaxTransportUnits) {
            this.target.noMaxTransportUnits = noMaxTransportUnits;
            return this;
        }

        public LocationBuilder withMaximumWeight(BigDecimal maximumWeight) {
            this.target.maximumWeight = maximumWeight;
            return this;
        }

        public LocationBuilder withLastMovement(LocalDateTime lastMovement) {
            this.target.lastMovement = lastMovement;
            return this;
        }

        public LocationBuilder withLocationGroupCountingActive(boolean locationGroupCountingActive) {
            this.target.locationGroupCountingActive = locationGroupCountingActive;
            return this;
        }

        public LocationBuilder withIncomingActive(boolean incomingActive) {
            this.target.incomingActive = incomingActive;
            return this;
        }

        public LocationBuilder withOutgoingActive(boolean outgoingActive) {
            this.target.outgoingActive = outgoingActive;
            return this;
        }

        public LocationBuilder withPlcState(int plcState) {
            this.target.plcState = plcState;
            return this;
        }

        public LocationBuilder withConsideredInAllocation(boolean consideredInAllocation) {
            this.target.consideredInAllocation = consideredInAllocation;
            return this;
        }

        public LocationBuilder withLocationType(LocationType locationType) {
            this.target.locationType = locationType;
            return this;
        }

        public LocationBuilder withGroup(String group) {
            this.target.group = group;
            return this;
        }

        public LocationBuilder withClassification(String classification) {
            this.target.classification = classification;
            return this;
        }

        public LocationBuilder withLocationGroup(LocationGroup locationGroup) {
            this.target.locationGroup = locationGroup;
            return this;
        }

        public LocationBuilder withMessages(Set<Message> messages) {
            this.target.messages = messages;
            return this;
        }

        public Location build() {
            return target;
        }
    }
}
