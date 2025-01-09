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
package org.openwms.common.transport.allocation.spi;

import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.reservation.ReservationService;
import org.openwms.common.transport.reservation.TransportUnitReservation;
import org.openwms.core.lang.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A SimpleGenericAllocator.
 *
 * @author Heiko Scherrer
 */
@TxService
class SimpleGenericAllocator implements GenericAllocator {

    private static final Logger ALLOCATION_LOGGER = LoggerFactory.getLogger("ALLOCATION");
    private final TransportUnitService transportUnitService;
    private final ReservationService reservationService;

    SimpleGenericAllocator(TransportUnitService transportUnitService, ReservationService reservationService) {
        this.transportUnitService = transportUnitService;
        this.reservationService = reservationService;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull List<TransportUnitAllocation> allocate(@NotNull List<Triple<String, Object, Class<?>>> searchAttributes, List<String> sourceLocationGroupNames) {
        if (ALLOCATION_LOGGER.isDebugEnabled()) {
            ALLOCATION_LOGGER.debug("Trying to allocate TransportUnits with the following search criteria [{}] in LocationGroups {}",
                    searchAttributes, sourceLocationGroupNames);
        }

        var transportUnitBKOpt = searchAttributes.stream().filter(sa -> "transportUnitBK".equals(sa.key())).findFirst();
        var result = new ArrayList<TransportUnitAllocation>(0);
        if (transportUnitBKOpt.isPresent()) {
            var transportUnit = transportUnitService.findByBarcode(transportUnitBKOpt.get().valueAs(String.class));
            if (ALLOCATION_LOGGER.isDebugEnabled()) {
                ALLOCATION_LOGGER.debug("Found TransportUnit with transportUnitBK [{}] for allocation", transportUnitBKOpt.get().key());
            }
            if (transportUnit.hasReservations()) {
                ALLOCATION_LOGGER.error("TransportUnit [{}] has reservation and cannot be allocated", transportUnit.getBarcode());
                return result;
            }
            var reservation = new TransportUnitReservation(transportUnit, UUID.randomUUID().toString());
            reservationService.saveReservation(reservation);
            ALLOCATION_LOGGER.debug("A reservation [{}] has been added for the TransportUnit [{}]", reservation, transportUnit);
            result.add(TransportUnitAllocation.AllocationBuilder.anAllocation()
                    .transportUnit(transportUnit)
                    .actualLocation(transportUnit.getActualLocation())
                    .reservationId(reservation.getReservedBy())
                    .build()
            );
        }
        if (ALLOCATION_LOGGER.isDebugEnabled()) {
            ALLOCATION_LOGGER.debug("Allocated TransportUnit [{}] for allocation", result);
        }
        return result;
    }
}
