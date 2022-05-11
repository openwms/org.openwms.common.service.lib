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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A LocationVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationVO extends AbstractBase<LocationVO> implements TargetVO, Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.common.location-v1+json";
    /** HTTP media type representation. */
    public static final String MEDIA_TYPE_OPT = "application/vnd.openwms.common.location-opt-v1+json";

    /** The persistent technical key of the {@code Location}. */
    @JsonProperty("pKey")
    private String pKey;

    /** Unique natural key. */
    @JsonProperty("locationId")
    @NotEmpty
    private String locationId;

    /** The ID of the {@code Account}, the {@code Location} is assigned to. */
    @JsonProperty("accountId")
    private String accountId;

    /** PLC code of the {@code Location}. */
    @JsonProperty("plcCode")
    @NotEmpty(groups = ValidationGroups.Create.class)
    private String plcCode;

    /** ERP code of the {@code Location}. */
    @JsonProperty("erpCode")
    @NotEmpty(groups = ValidationGroups.Create.class)
    private String erpCode;

    /** Sort order index used by strategies for putaway, or picking. */
    @JsonProperty("sortOrder")
    private Integer sortOrder;

    /** Might be assigned to a particular zone in stock. */
    @JsonProperty("stockZone")
    private String stockZone;

    /** Whether the {@code Location} is enabled for incoming movements (read-only). */
    @JsonProperty("incomingActive")
    private Boolean incomingActive;

    /** Whether the {@code Location} is enabled for outgoing movements (read-only). */
    @JsonProperty("outgoingActive")
    private Boolean outgoingActive;

    /** The current state, set by the PLC system (read-only). */
    @JsonProperty("plcState")
    private Integer plcState;

    /** The name of the {@code LocationType} the {@code Location} belongs to. */
    @JsonProperty("type")
    @NotEmpty(groups = ValidationGroups.Create.class)
    private String type;

    /** The {@code LocationGroup} the {@code Location} belongs to. */
    @JsonProperty("locationGroupName")
    @NotEmpty(groups = ValidationGroups.Create.class)
    private String locationGroupName;

    /*~-------------------- constructors --------------------*/
    protected LocationVO() {
        super();
    }

    public LocationVO(String locationId) {
        super();
        this.locationId = locationId;
    }

    /*~-------------------- methods --------------------*/
    /**
     * Checks whether the Location is blocked for incoming goods.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}
     */
    @JsonIgnore
    public boolean isInfeedBlocked() {
        return !incomingActive;
    }

    /**
     * Checks whether the Location is blocked for outgoing goods.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}
     */
    @JsonIgnore
    public boolean isOutfeedBlocked() {
        return !outgoingActive;
    }

    /*~-------------------- accessors --------------------*/
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getLocationGroupName() {
        return locationGroupName;
    }

    public void setLocationGroupName(String locationGroupName) {
        this.locationGroupName = locationGroupName;
    }

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getPlcCode() {
        return plcCode;
    }

    public void setPlcCode(String plcCode) {
        this.plcCode = plcCode;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIncomingActive() {
        return incomingActive;
    }

    public void setIncomingActive(Boolean incomingActive) {
        this.incomingActive = incomingActive;
    }

    public Boolean getOutgoingActive() {
        return outgoingActive;
    }

    public void setOutgoingActive(Boolean outgoingActive) {
        this.outgoingActive = outgoingActive;
    }

    public Integer getPlcState() {
        return plcState;
    }

    public void setPlcState(Integer plcState) {
        this.plcState = plcState;
    }

    public String getStockZone() {
        return stockZone;
    }

    public void setStockZone(String stockZone) {
        this.stockZone = stockZone;
    }

    /*~-------------------- overrides --------------------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return locationId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", LocationVO.class.getSimpleName() + "[", "]").add("pKey='" + pKey + "'").add("locationId='" + locationId + "'").add("locationGroupName='" + locationGroupName + "'").add("plcCode='" + plcCode + "'").add("erpCode='" + erpCode + "'").add("incomingActive=" + incomingActive).add("outgoingActive=" + outgoingActive).toString();
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationVO that = (LocationVO) o;
        return Objects.equals(pKey, that.pKey) &&
                Objects.equals(locationId, that.locationId) &&
                Objects.equals(accountId, that.accountId) &&
                Objects.equals(locationGroupName, that.locationGroupName) &&
                Objects.equals(erpCode, that.erpCode) &&
                Objects.equals(plcCode, that.plcCode) &&
                Objects.equals(incomingActive, that.incomingActive) &&
                Objects.equals(outgoingActive, that.outgoingActive) &&
                Objects.equals(plcState, that.plcState) &&
                Objects.equals(stockZone, that.stockZone) ;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, locationId, accountId, locationGroupName, erpCode, plcCode, incomingActive, outgoingActive, plcState, stockZone);
    }
}
