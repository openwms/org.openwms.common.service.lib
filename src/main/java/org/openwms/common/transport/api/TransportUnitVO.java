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
package org.openwms.common.transport.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;
import org.openwms.common.location.api.LocationVO;
import org.openwms.core.units.api.Weight;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * A TransportUnitVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransportUnitVO extends AbstractBase<TransportUnitVO> implements Serializable {

    /** The persistent key. */
    @JsonProperty("pKey")
    private String pKey;
    /** Unique natural key. */
    @NotEmpty(message = "{owms.common.common.tu.barcode}")
    @JsonProperty("barcode")
    private String barcode;
    /** A {@code TransportUnit} may belong to a group of {@code TransportUnits}. */
    @JsonProperty("groupId")
    private String groupId;
    @NotNull(message = "{owms.common.common.tu.actualLocation}", groups = ValidationGroups.TransportUnit.Create.class)
    @JsonProperty("actualLocation")
    private LocationVO actualLocation;
    @JsonProperty("toLocation")
    private LocationVO targetLocation;
    /** The state of the TransportUnit. */
    @JsonProperty("state")
    private String state;
    @NotEmpty(message = "{owms.common.common.tu.transportUnitTypeName}", groups = {ValidationGroups.TransportUnit.Create.class, ValidationGroups.TransportUnit.WithTuT.class})
    @JsonProperty("transportUnitTypeName")
    private String transportUnitType;
    @JsonProperty("length")
    private Integer length;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("weight")
    private Weight weight;
    @JsonProperty("actualLocationDate")
    private Date actualLocationDate;
    @JsonProperty("createDate")
    private Date createDate;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    protected TransportUnitVO() {
    }

    public TransportUnitVO(String barcode) {
        this.barcode = barcode;
    }

    public TransportUnitVO(String barcode, String transportUnitType, LocationVO actualLocation) {
        this.barcode = barcode;
        this.transportUnitType = transportUnitType;
        this.actualLocation = actualLocation;
    }

    private TransportUnitVO(Builder builder) {
        barcode = builder.barcode;
        actualLocation = builder.actualLocation;
        transportUnitType = builder.transportUnitType;
        length = builder.length;
        width = builder.width;
        height = builder.height;
        actualLocationDate = builder.actualLocationDate;
        createDate = builder.createDate;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(String transportUnitType) {
        this.transportUnitType = transportUnitType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public Date getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(Date actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitVO)) return false;
        if (!super.equals(o)) return false;
        TransportUnitVO that = (TransportUnitVO) o;
        return Objects.equals(barcode, that.barcode) &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(actualLocation, that.actualLocation) &&
                Objects.equals(transportUnitType, that.transportUnitType) &&
                Objects.equals(length, that.length) &&
                Objects.equals(width, that.width) &&
                Objects.equals(height, that.height) &&
                Objects.equals(actualLocationDate, that.actualLocationDate) &&
                Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), barcode, groupId, actualLocation, transportUnitType, length, width, height, actualLocationDate, createDate);
    }

    /*~-------------------- Builder --------------------*/
    public static final class Builder {
        private @NotEmpty String barcode;
        private LocationVO actualLocation;
        private String target;
        private @NotEmpty(groups = ValidationGroups.TransportUnit.WithTuT.class) String transportUnitType;
        private Integer length;
        private Integer width;
        private Integer height;
        private Date actualLocationDate;
        private Date createDate;

        private Builder() {
        }

        public Builder barcode(@NotEmpty String val) {
            barcode = val;
            return this;
        }

        public Builder actualLocation(LocationVO val) {
            actualLocation = val;
            return this;
        }

        public Builder target(String val) {
            target = val;
            return this;
        }

        public Builder transportUnitType(@NotEmpty(groups = ValidationGroups.TransportUnit.WithTuT.class) String val) {
            transportUnitType = val;
            return this;
        }

        public Builder length(Integer val) {
            length = val;
            return this;
        }

        public Builder width(Integer val) {
            width = val;
            return this;
        }

        public Builder height(Integer val) {
            height = val;
            return this;
        }

        public Builder actualLocationDate(Date val) {
            actualLocationDate = val;
            return this;
        }

        public Builder createDate(Date val) {
            createDate = val;
            return this;
        }

        public TransportUnitVO build() {
            return new TransportUnitVO(this);
        }
    }
}
