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

import java.util.List;

/**
 * A StockService.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface StockService {

    /**
     * Find and return all {@code Location}s that belong to the {@code LocationGroup}s
     * identified by the given {@code stockLocationGroupNames} and that match the applied
     * filter criteria.
     *
     * @param stockLocationGroupNames The names of the LocationGroups to search Locations
     * for
     * @param groupStateIn If {@literal null} this criterion is not applied, otherwise
     * only Locations are considered that match the demanded groupStateIn
     * @param groupStateOut If {@literal null} this criterion is not applied, otherwise
     * only Locations are considered that match the demanded groupStateOut
     * @param count A number of Locations to return. Useful to limit the result set
     * @return All Locations
     * @throws org.ameba.exception.NotFoundException if no Locations exist
     */
    List<Location> findAvailableStockLocations(List<String> stockLocationGroupNames, LocationGroupState groupStateIn, LocationGroupState groupStateOut, int count);
}
