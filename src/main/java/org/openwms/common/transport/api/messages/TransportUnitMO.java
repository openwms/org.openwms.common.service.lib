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
package org.openwms.common.transport.api.messages;

import org.openwms.common.transport.api.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * A TransportUnitMO is a Message Object representing a {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitMO implements Serializable {

    /** The persistent key of TransportUnit. */
    @NotEmpty(groups = {ValidationGroups.TransportUnit.Request.class, ValidationGroups.TransportUnit.Remove.class})
    private String pKey;
    /** The business key of the TransportUnit. */
    @NotEmpty(groups = {
            ValidationGroups.TransportUnit.ChangeTarget.class,
            ValidationGroups.TransportUnit.Create.class,
            ValidationGroups.TransportUnit.Modified.class
    })
    private String barcode;
    /** The actualLocationDate of the TransportUnit. */
    private Date actualLocationDate;
    /** The state of the TransportUnit. */
    private String state;
    /** The actualLocation of the TransportUnit. */
    @NotNull(groups = {
            ValidationGroups.TransportUnit.Create.class,
            ValidationGroups.TransportUnit.Modified.class
    })
    private String actualLocation;
    /** The plcCode of the TransportUnit. */
    private String plcCode;
    /** The targetLocation of the TransportUnit. */
    @NotEmpty(groups = ValidationGroups.TransportUnit.ChangeTarget.class)
    private String targetLocation;
    /** The transportUnitType of the TransportUnit. */
    @Valid
    @NotNull(groups = {
            ValidationGroups.TransportUnit.Modified.class
    })
    private TransportUnitTypeMO transportUnitType;
    /** The business key of the parent TransportUnit. */
    private String parent;

    /**
     * Required Default constructor.
     */
    protected TransportUnitMO() { }

    private TransportUnitMO(Builder builder) {
        setpKey(builder.pKey);
        setBarcode(builder.barcode);
        setActualLocationDate(builder.actualLocationDate);
        setState(builder.state);
        setActualLocation(builder.actualLocation);
        setPlcCode(builder.plcCode);
        setTargetLocation(builder.targetLocation);
        setTransportUnitType(builder.transportUnitType);
        setParent(builder.parent);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

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

    public Date getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(Date actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getPlcCode() {
        return plcCode;
    }

    public void setPlcCode(String plcCode) {
        this.plcCode = plcCode;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public TransportUnitTypeMO getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(TransportUnitTypeMO transportUnitType) {
        this.transportUnitType = transportUnitType;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransportUnitMO.class.getSimpleName() + "[", "]").add("pKey='" + pKey + "'").add("barcode='" + barcode + "'").add("actualLocationDate=" + actualLocationDate).add("state='" + state + "'").add("actualLocation='" + actualLocation + "'").add("plcCode='" + plcCode + "'").add("targetLocation='" + targetLocation + "'").add("transportUnitType=" + transportUnitType).add("parent='" + parent + "'").toString();
    }

    public static final class Builder {
        private String pKey;
        private String barcode;
        private Date actualLocationDate;
        private String state;
        private String actualLocation;
        private String plcCode;
        private String targetLocation;
        private TransportUnitTypeMO transportUnitType;
        private String parent;

        private Builder() {
        }

        public Builder withPKey(String val) {
            pKey = val;
            return this;
        }

        public Builder withBarcode(String val) {
            barcode = val;
            return this;
        }

        public Builder withActualLocationDate(Date val) {
            actualLocationDate = val;
            return this;
        }

        public Builder withState(String val) {
            state = val;
            return this;
        }

        public Builder withActualLocation(String val) {
            actualLocation = val;
            return this;
        }

        public Builder withPlcCode(String val) {
            plcCode = val;
            return this;
        }

        public Builder withTargetLocation(String val) {
            targetLocation = val;
            return this;
        }

        public Builder withTransportUnitType(TransportUnitTypeMO val) {
            transportUnitType = val;
            return this;
        }

        public Builder withParent(String val) {
            parent = val;
            return this;
        }

        public TransportUnitMO build() {
            return new TransportUnitMO(this);
        }
    }


}
