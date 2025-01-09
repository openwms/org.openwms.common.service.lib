/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.transport.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ameba.http.AbstractBase;
import org.openwms.common.location.api.LocationVO;
import org.openwms.core.units.api.Weight;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A TransportUnitVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransportUnitVO extends AbstractBase<TransportUnitVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.transport-unit-v1+json";

    /** The persistent key. */
    @NotBlank(groups = ValidationGroups.TransportUnit.Update.class)
    @JsonProperty("pKey")
    private String pKey;

    /** Unique natural key. */
    @NotBlank(message = "{owms.common.common.tu.barcode}", groups = {ValidationGroups.TransportUnit.Create.class, ValidationGroups.TransportUnit.Update.class})
    @JsonProperty("barcode")
    private String barcode;

    /** A {@code TransportUnit} may belong to a group of {@code TransportUnits}. */
    @JsonProperty("groupId")
    private String groupId;

    /** The current {@code Location} of the {@code TransportUnit}. */
    @NotNull(message = "{owms.common.common.tu.actualLocation}", groups = {ValidationGroups.TransportUnit.Create.class, ValidationGroups.TransportUnit.Update.class})
    @JsonProperty("actualLocation")
    private LocationVO actualLocation;

    /** The target {@code Location} of the {@code TransportUnit}. */
    @JsonProperty("toLocation")
    private LocationVO targetLocation;

    /** The state of the TransportUnit. */
    @NotBlank(message = "{owms.common.common.tu.state}", groups = {ValidationGroups.TransportUnit.Update.class})
    @JsonProperty("state")
    private String state;

    /** The {@code TransportUnitType} of the {@code TransportUnit}. */
    @NotNull(message = "{owms.common.common.tu.transportUnitType}", groups = {ValidationGroups.TransportUnit.Create.class, ValidationGroups.TransportUnit.WithTuT.class})
    @JsonProperty("transportUnitType")
    private TransportUnitTypeVO transportUnitType;

    /** Weight of the {@code TransportUnit}. */
    @JsonProperty("weight")
    private Weight weight;

    /** Date when the {@code TransportUnit} has been moved to the current {@code Location}. */
    @JsonProperty("actualLocationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT_ZULU) // required
    private LocalDateTime actualLocationDate;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    public TransportUnitVO() {}

    //@ConstructorProperties({"barcode"})
    public TransportUnitVO(String barcode) {
        this.barcode = barcode;
    }

    //@ConstructorProperties({"barcode", "transportUnitType", "actualLocation"})
    public TransportUnitVO(String barcode, TransportUnitTypeVO transportUnitType, LocationVO actualLocation) {
        this.barcode = barcode;
        this.transportUnitType = transportUnitType;
        this.actualLocation = actualLocation;
    }

    private TransportUnitVO(Builder builder) {
        barcode = builder.barcode;
        actualLocation = builder.actualLocation;
        transportUnitType = builder.transportUnitType;
        actualLocationDate = builder.actualLocationDate;
        super.setCreateDt(builder.createDate);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder() {
        return new Builder();
    }

    /*~-------------------- accessors --------------------*/
    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public LocationVO getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(LocationVO targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public TransportUnitTypeVO getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(TransportUnitTypeVO transportUnitType) {
        this.transportUnitType = transportUnitType;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public LocalDateTime getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(LocalDateTime actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitVO)) return false;
        if (!super.equals(o)) return false;
        var that = (TransportUnitVO) o;
        return Objects.equals(pKey, that.pKey) && Objects.equals(barcode, that.barcode) && Objects.equals(groupId, that.groupId) && Objects.equals(actualLocation, that.actualLocation) && Objects.equals(targetLocation, that.targetLocation) && Objects.equals(state, that.state) && Objects.equals(transportUnitType, that.transportUnitType) && Objects.equals(weight, that.weight) && Objects.equals(actualLocationDate, that.actualLocationDate);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, barcode, groupId, actualLocation, targetLocation, state, transportUnitType, weight, actualLocationDate);
    }

    /*~-------------------- Builder --------------------*/
    public static final class Builder {
        private String barcode;
        private LocationVO actualLocation;
        private TransportUnitTypeVO transportUnitType;
        private LocalDateTime actualLocationDate;
        private LocalDateTime createDate;

        private Builder() {
        }

        public Builder barcode(String val) {
            barcode = val;
            return this;
        }

        public Builder actualLocation(LocationVO val) {
            actualLocation = val;
            return this;
        }

        public Builder transportUnitType(TransportUnitTypeVO val) {
            transportUnitType = val;
            return this;
        }

        public Builder actualLocationDate(LocalDateTime val) {
            actualLocationDate = val;
            return this;
        }

        public Builder createDate(LocalDateTime val) {
            createDate = val;
            return this;
        }

        public TransportUnitVO build() {
            return new TransportUnitVO(this);
        }
    }
}
