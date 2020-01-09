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
import org.ameba.http.AbstractBase;
import org.openwms.common.location.api.LocationVO;

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
public class TransportUnitVO extends AbstractBase implements Serializable {

    @NotEmpty
    private String barcode;
    @NotNull(groups = ValidationGroups.TransportUnit.Create.class)
    private LocationVO actualLocation;
    private String target;
    @NotEmpty(groups = {ValidationGroups.TransportUnit.Create.class, ValidationGroups.TransportUnit.WithTuT.class})
    private String transportUnitType;
    private Integer length;
    private Integer width;
    private Integer height;
    private String actualPlcCode;
    private Date actualLocationDate;

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
        target = builder.target;
        transportUnitType = builder.transportUnitType;
        length = builder.length;
        width = builder.width;
        height = builder.height;
        actualPlcCode = builder.actualPlcCode;
        actualLocationDate = builder.actualLocationDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder() {
        return new Builder();
    }

    /*~-------------------- accessors --------------------*/
    public String getBarcode() {
        return barcode;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    public String getActualPlcCode() {
        return actualPlcCode;
    }

    public void setActualPlcCode(String actualPlcCode) {
        this.actualPlcCode = actualPlcCode;
    }

    public Date getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(Date actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitVO)) return false;
        if (!super.equals(o)) return false;
        TransportUnitVO that = (TransportUnitVO) o;
        return Objects.equals(barcode, that.barcode) &&
                Objects.equals(actualLocation, that.actualLocation) &&
                Objects.equals(target, that.target) &&
                Objects.equals(transportUnitType, that.transportUnitType) &&
                Objects.equals(length, that.length) &&
                Objects.equals(width, that.width) &&
                Objects.equals(height, that.height) &&
                Objects.equals(actualPlcCode, that.actualPlcCode) &&
                Objects.equals(actualLocationDate, that.actualLocationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), barcode, actualLocation, target, transportUnitType, length, width, height, actualPlcCode, actualLocationDate);
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
        private String actualPlcCode;
        private Date actualLocationDate;

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

        public Builder actualPlcCode(String val) {
            actualPlcCode = val;
            return this;
        }

        public Builder actualLocationDate(Date val) {
            actualLocationDate = val;
            return this;
        }

        public TransportUnitVO build() {
            return new TransportUnitVO(this);
        }
    }
}
