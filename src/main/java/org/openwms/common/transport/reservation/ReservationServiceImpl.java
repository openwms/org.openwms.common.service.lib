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
package org.openwms.common.transport.reservation;

import jakarta.validation.constraints.NotBlank;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

/**
 * A ReservationServiceImpl.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class ReservationServiceImpl implements ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);
    private final ReservationRepository reservationRepository;
    private final TransportUnitReservationRepository transportUnitReservationRepository;

    ReservationServiceImpl(ReservationRepository reservationRepository, TransportUnitReservationRepository transportUnitReservationRepository) {
        this.reservationRepository = reservationRepository;
        this.transportUnitReservationRepository = transportUnitReservationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public void saveReservation(TransportUnitReservation reservation) {
        transportUnitReservationRepository.save(reservation);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public void acknowledgeReservation(@NotBlank String reservationId, @NotBlank String acknowledgeId) {
        var reservations = reservationRepository.findByReservedBy(reservationId);
        if (reservations.isEmpty()) {
            LOGGER.error("No reserved TransportUnits with reservationId [{}] so nothing to acknowledge", reservationId);
            return;
        }
        reservations.forEach(r -> {
            var currentReservationId = r.getReservedBy();
            r.setReservedBy(acknowledgeId);
            reservationRepository.save(r);
            LOGGER.debug("Acknowledged reservation [{}] on TransportUnit [{}] with acknowledgeId [{}]", currentReservationId,
                    r.getTransportUnit().getPersistentKey(), acknowledgeId);
        });
    }
}
