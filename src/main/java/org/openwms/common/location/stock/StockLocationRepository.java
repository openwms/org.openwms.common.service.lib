/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2018 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.location.stock;

import org.openwms.common.location.Location;
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
            "and l.locationGroup.groupStateIn = org.openwms.common.location.LocationGroupState.AVAILABLE " +
            "and l.incomingActive = true " +
            "and l not in (select distinct t.actualLocation from TransportUnit t) " +
            "order by l.locationId.area, l.locationId.aisle, l.locationId.x, l.locationId.y, l.locationId.z DESC")
    List<Location> findBy(@Param("locationGroupNames") List<String> locationGroupNames);
}
