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
package org.openwms.common.location.stock;

import org.openwms.common.location.Location;
import org.openwms.common.location.api.LocationGroupState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A StockLocationRepository.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
interface StockLocationRepository extends JpaRepository<Location, Long> {

    @Query("select l " +
            "from Location l " +
            "where l.locationGroup.name in :locationGroupNames " +
            "and ((l.locationGroup.groupStateIn = :groupStateIn and l.incomingActive = true) or :groupStateIn is null) " +
            "and ((l.locationGroup.groupStateOut = :groupStateOut and l.outgoingActive = true) or :groupStateOut is null) " +
            "and l not in (select distinct t.actualLocation from TransportUnit t) " +
            "and l not in (select distinct t.targetLocation from TransportUnit t) " +
            "order by l.locationId.area, l.locationId.aisle, l.locationId.x, l.locationId.y, l.locationId.z DESC")
    List<Location> findBy(Pageable pageable, @Param("locationGroupNames") List<String> locationGroupNames,  @Param("groupStateIn") LocationGroupState groupStateIn, @Param("groupStateOut") LocationGroupState groupStateOut);
}
