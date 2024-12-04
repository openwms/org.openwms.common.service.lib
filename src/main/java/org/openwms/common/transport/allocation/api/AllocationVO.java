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
package org.openwms.common.transport.allocation.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A AllocationVO.
 *
 * @author Heiko Scherrer
 */
public class AllocationVO implements Serializable {

    @JsonProperty("transportUnitBK")
    private String transportUnitBK;
    @JsonProperty("actualErpCode")
    private String actualErpCode;
    @JsonProperty("reservationId")
    private String reservationId;

    private AllocationVO() {}

    private AllocationVO(Builder builder) {
        setTransportUnitBK(builder.transportUnitBK);
        setActualErpCode(builder.actualErpCode);
        setReservationId(builder.reservationId);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public void setTransportUnitBK(String transportUnitBK) {
        this.transportUnitBK = transportUnitBK;
    }

    public String getActualErpCode() {
        return actualErpCode;
    }

    public void setActualErpCode(String actualErpCode) {
        this.actualErpCode = actualErpCode;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public static final class Builder {
        private String transportUnitBK;
        private String actualErpCode;
        private String reservationId;

        private Builder() {
        }

        public Builder transportUnitBK(String val) {
            transportUnitBK = val;
            return this;
        }

        public Builder actualErpCode(String val) {
            actualErpCode = val;
            return this;
        }

        public Builder reservationId(String val) {
            reservationId = val;
            return this;
        }

        public AllocationVO build() {
            return new AllocationVO(this);
        }
    }
}
