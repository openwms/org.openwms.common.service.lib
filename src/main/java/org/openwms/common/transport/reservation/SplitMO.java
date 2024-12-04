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
package org.openwms.common.transport.reservation;

import java.io.Serializable;
import java.util.Objects;

/**
 * A SplitMO.
 *
 * @author Heiko Scherrer
 */
public class SplitMO implements Serializable {

    private String reservationId;
    private String shippingOrderPositionPKey;
    private int splitNo;

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getShippingOrderPositionPKey() {
        return shippingOrderPositionPKey;
    }

    public void setShippingOrderPositionPKey(String shippingOrderPositionPKey) {
        this.shippingOrderPositionPKey = shippingOrderPositionPKey;
    }

    public int getSplitNo() {
        return splitNo;
    }

    public void setSplitNo(int splitNo) {
        this.splitNo = splitNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplitMO splitMO = (SplitMO) o;
        return splitNo == splitMO.splitNo && Objects.equals(reservationId, splitMO.reservationId) && Objects.equals(shippingOrderPositionPKey, splitMO.shippingOrderPositionPKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, shippingOrderPositionPKey, splitNo);
    }
}
