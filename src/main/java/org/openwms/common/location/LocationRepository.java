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
package org.openwms.common.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * A LocationRepository adds particular functionality regarding {@link Location} entity classes.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationId(LocationPK locationId);

    @Query("select l from Location l where l.locationGroup.name in :locationGroupNames")
    List<Location> findByLocationGroup_Name(@Param("locationGroupNames") List<String> locationGroupNames);
}