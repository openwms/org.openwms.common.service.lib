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
package org.openwms.common.transport.api.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.common.transport.api.ValidationGroups;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.StringJoiner;

import static org.openwms.common.transport.api.TransportApiConstants.DATETIME_FORMAT_ZULU;

/**
 * A TransportUnitMO is a Message Object (MO) that represents a {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitMO implements Serializable {

    /** The persistent key of TransportUnit. */
    @NotBlank(groups = {
            ValidationGroups.TransportUnit.Request.class,
            ValidationGroups.TransportUnit.Remove.class
    })
    private String pKey;

    /** The business key of the TransportUnit. */
    @NotBlank(groups = {
            ValidationGroups.TransportUnit.ChangeTarget.class,
            ValidationGroups.TransportUnit.Create.class,
            ValidationGroups.TransportUnit.Modified.class
    })
    private String barcode;

    /** The actualLocationDate of the TransportUnit. */
    @JsonProperty("actualLocationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT_ZULU) // required
    private LocalDateTime actualLocationDate;

    /** The state of the TransportUnit. */
    private String state;

    /** The actualLocation of the TransportUnit. */
    @NotNull(groups = {
            ValidationGroups.TransportUnit.Create.class,
            ValidationGroups.TransportUnit.Modified.class
    })
    private LocationMO actualLocation;

    /** The targetLocation of the TransportUnit. */
    @NotNull(groups = {
            ValidationGroups.TransportUnit.ChangeTarget.class
    })
    private LocationMO targetLocation;

    /** The transportUnitType of the TransportUnit. */
    @Valid
    @NotNull(groups = {
            ValidationGroups.TransportUnit.Modified.class
    })
    private TransportUnitTypeMO transportUnitType;

    /** The business key of the parent TransportUnit. */
    private String parent;

    /**  Required Default constructor. */
    public TransportUnitMO() { }

    private TransportUnitMO(Builder builder) {
        setpKey(builder.pKey);
        setBarcode(builder.barcode);
        setActualLocationDate(builder.actualLocationDate);
        setState(builder.state);
        setActualLocation(builder.actualLocation);
        setTargetLocation(builder.targetLocation);
        setTransportUnitType(builder.transportUnitType);
        setParent(builder.parent);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasTargetLocation() {
        return this.targetLocation != null;
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

    public LocalDateTime getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(LocalDateTime actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocationMO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationMO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public LocationMO getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(LocationMO targetLocation) {
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

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", TransportUnitMO.class.getSimpleName() + "[", "]")
                .add("pKey='" + pKey + "'")
                .add("barcode='" + barcode + "'")
                .add("actualLocationDate=" + actualLocationDate)
                .add("state='" + state + "'")
                .add("actualLocation=" + actualLocation)
                .add("targetLocation=" + targetLocation)
                .add("transportUnitType=" + transportUnitType)
                .add("parent='" + parent + "'")
                .toString();
    }

    public static final class Builder {
        private String pKey;
        private String barcode;
        private LocalDateTime actualLocationDate;
        private String state;
        private LocationMO actualLocation;
        private LocationMO targetLocation;
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

        public Builder withActualLocationDate(LocalDateTime val) {
            actualLocationDate = val;
            return this;
        }

        public Builder withState(String val) {
            state = val;
            return this;
        }

        public Builder withActualLocation(LocationMO val) {
            actualLocation = val;
            return this;
        }

        public Builder withTargetLocation(LocationMO val) {
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
