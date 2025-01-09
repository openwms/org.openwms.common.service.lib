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
import jakarta.validation.constraints.NotNull;


/**
 * A ReservationService manages reservations of {@code TransportUnit}s.
 *
 * @author Heiko Scherrer
 */
public interface ReservationService {

    /**
     * Save a {@link TransportUnitReservation}.
     *
     * @param reservation The instance to create or update
     */
    void saveReservation(@NotNull TransportUnitReservation reservation);

    /**
     * Acknowledge a previously set reservation on a {@code TransportUnit}. If no {@code TransportUnit} is reserved with the
     * {@code reservationId} the implementation might silently return without further action.
     *
     * @param reservationId The previously set reservationId
     * @param acknowledgeId The acknowledgeId to replace the reservationId with
     */
    void acknowledgeReservation(@NotBlank String reservationId, @NotBlank String acknowledgeId);
}
