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
package org.openwms.common.transport.allocation.spi;

import org.openwms.common.location.Location;
import org.openwms.common.transport.TransportUnit;

import java.util.StringJoiner;

/**
 * An TransportUnitAllocation.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitAllocation {

    private TransportUnit transportUnit;
    private Location actualLocation;
    private String reservationId;

    public TransportUnit getTransportUnit() {
        return transportUnit;
    }

    public void setTransportUnit(TransportUnit transportUnit) {
        this.transportUnit = transportUnit;
    }

    public Location getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(Location actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransportUnitAllocation.class.getSimpleName() + "[", "]")
                .add("transportUnit=" + transportUnit)
                .add("actualLocation=" + actualLocation)
                .add("reservationId='" + reservationId + "'")
                .toString();
    }

    public static final class AllocationBuilder {
        private TransportUnit transportUnit;
        private Location actualLocation;
        private String reservationId;

        private AllocationBuilder() {
        }

        public static AllocationBuilder anAllocation() {
            return new AllocationBuilder();
        }

        public AllocationBuilder transportUnit(TransportUnit transportUnit) {
            this.transportUnit = transportUnit;
            return this;
        }

        public AllocationBuilder actualLocation(Location actualLocation) {
            this.actualLocation = actualLocation;
            return this;
        }

        public AllocationBuilder reservationId(String reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public TransportUnitAllocation build() {
            var allocation = new TransportUnitAllocation();
            allocation.setTransportUnit(transportUnit);
            allocation.setActualLocation(actualLocation);
            allocation.setReservationId(reservationId);
            return allocation;
        }
    }
}
