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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class LocationVO extends RepresentationModel<LocationVO> implements TargetVO, Serializable {

    @JsonProperty("pKey")
    private String pKey;
    @JsonProperty("locationId")
    @NotEmpty
    private String locationId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("accountId")
    private String accountId;
    @JsonProperty("locationGroupName")
    private String locationGroupName;
    @JsonProperty("erpCode")
    private String erpCode;
    @JsonProperty("plcCode")
    private String plcCode;
    @JsonProperty("incomingActive")
    private Boolean incomingActive;
    @JsonProperty("outgoingActive")
    private Boolean outgoingActive;
    @JsonProperty("plcState")
    private Integer plcState;
    @JsonProperty("stockZone")
    private String stockZone;

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
    public Boolean isInfeedBlocked() {
        return !incomingActive;
    }

    /**
     * Checks whether the Location is blocked for outgoing goods.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}
     */
    @JsonIgnore
    public Boolean isOutfeedBlocked() {
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
