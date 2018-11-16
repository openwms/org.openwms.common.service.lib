/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.transport;

import org.openwms.common.location.LocationPK;

import java.util.List;

/**
 * A TransportService offers functionality to create, read, update and delete {@link TransportUnit}s. Additionally it defines useful methods
 * regarding the general handling with {@link TransportUnit}s.
 *
 * @param <T> Any kind of {@link TransportUnit}
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface TransportUnitService<T extends TransportUnit> {

    /**
     * Create a new {@link TransportUnit} with the type {@link TransportUnitType} placed on an initial {@code Location}. The new {@link
     * TransportUnit} has the given {@link Barcode} as identifier.
     *
     * @param barcode           {@link Barcode} of the new {@link TransportUnit}
     * @param transportUnitType The type of the new {@link TransportUnit}
     * @param actualLocation    The {@code Location} where the {@link TransportUnit} is placed on
     * @return The new created {@link TransportUnit} instance
     */
    T create(Barcode barcode, TransportUnitType transportUnitType, LocationPK actualLocation, Boolean strict);

    /**
     * Create a new {@link TransportUnit} with the type {@link TransportUnitType} placed on an initial {@code Location}. The new {@link
     * TransportUnit} has the given {@link Barcode} as identifier.
     *
     * @param barcode           {@link Barcode} of the new {@link TransportUnit}
     * @param transportUnitType The type of the new {@link TransportUnit}
     * @param actualLocation    The {@code Location} where the {@link TransportUnit} is placed on
     * @return The new created {@link TransportUnit} instance
     */
    T create(Barcode barcode, String transportUnitType, String actualLocation, Boolean strict);

    T update(Barcode barcode, T tu);

    /**
     * Move a {@link TransportUnit} identified by its {@link Barcode} to the given target {@code Location} identified by the {@code
     * LocationPK}.
     *
     * @param barcode          {@link Barcode} of the {@link TransportUnit} to move
     * @param targetLocationPK Unique identifier of the target {@code Location}
     */
    TransportUnit moveTransportUnit(Barcode barcode, LocationPK targetLocationPK);

    /**
     * Delete already persisted {@link TransportUnit}s from the persistence storage. It is not allowed in all cases to delete a {@link
     * TransportUnit} , potentially an active {@code TransportOrder} exists or Inventory is still linked with the {@code transportUnit}.
     *
     * @param transportUnits The collection of {@link TransportUnit}s to delete
     */
    void deleteTransportUnits(List<T> transportUnits);

    /**
     * Returns a List of all {@link TransportUnit}s.
     *
     * @return A List of all {@link TransportUnit}s
     * @deprecated Move to UI specific interface
     */
    @Deprecated
    List<T> findAll();

    /**
     * Find and return a {@link TransportUnit} with a particular {@link Barcode}.
     *
     * @param barcode {@link Barcode} of the {@link TransportUnit} to search for
     * @return The {@link TransportUnit} or {@literal null} when no {@link TransportUnit} with this {@link Barcode} exists
     * @throws org.ameba.exception.NotFoundException may throw if not found
     */
    T findByBarcode(Barcode barcode);

    List<T> findOnLocation(String actualLocation);
}