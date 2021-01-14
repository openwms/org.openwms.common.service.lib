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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A TransportUnitType is a Message Object representing a {@code TransportUnitType}.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitTypeMO implements Serializable {

    @NotNull(groups = {
            ValidationGroups.TransportUnit.Create.class,
            ValidationGroups.TransportUnit.Modified.class
    })
    private String type;
    private int length;
    private int width;
    private int height;
    private BigDecimal weightTare;
    private BigDecimal weightMax;
    private BigDecimal payload;
    private String compatibility;

    /**
     * Required Default constructor.
     */
    protected TransportUnitTypeMO() { }

    private TransportUnitTypeMO(Builder builder) {
        setType(builder.type);
        setLength(builder.length);
        setWidth(builder.width);
        setHeight(builder.height);
        setWeightTare(builder.weightTare);
        setWeightMax(builder.weightMax);
        setPayload(builder.payload);
        setCompatibility(builder.compatibility);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BigDecimal getWeightTare() {
        return weightTare;
    }

    public void setWeightTare(BigDecimal weightTare) {
        this.weightTare = weightTare;
    }

    public BigDecimal getWeightMax() {
        return weightMax;
    }

    public void setWeightMax(BigDecimal weightMax) {
        this.weightMax = weightMax;
    }

    public BigDecimal getPayload() {
        return payload;
    }

    public void setPayload(BigDecimal payload) {
        this.payload = payload;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public static final class Builder {
        private String type;
        private int length;
        private int width;
        private int height;
        private BigDecimal weightTare;
        private BigDecimal weightMax;
        private BigDecimal payload;
        private String compatibility;

        private Builder() {
        }

        public Builder type(@NotNull(groups = ValidationGroups.TransportUnit.Create.class) String val) {
            type = val;
            return this;
        }

        public Builder length(int val) {
            length = val;
            return this;
        }

        public Builder width(int val) {
            width = val;
            return this;
        }

        public Builder height(int val) {
            height = val;
            return this;
        }

        public Builder weightTare(BigDecimal val) {
            weightTare = val;
            return this;
        }

        public Builder weightMax(BigDecimal val) {
            weightMax = val;
            return this;
        }

        public Builder payload(BigDecimal val) {
            payload = val;
            return this;
        }

        public Builder compatibility(String val) {
            compatibility = val;
            return this;
        }

        public TransportUnitTypeMO build() {
            return new TransportUnitTypeMO(this);
        }
    }
}
