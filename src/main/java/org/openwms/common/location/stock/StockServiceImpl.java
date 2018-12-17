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

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.location.Location;
import org.openwms.common.location.api.LocationGroupState;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * A StockServiceImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class StockServiceImpl implements StockService {

    private final StockLocationRepository stockLocationRepository;

    StockServiceImpl(StockLocationRepository stockLocationRepository) {
        this.stockLocationRepository = stockLocationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public List<Location> findAvailableStockLocations(List<String> stockLocationGroupNames, LocationGroupState groupStateIn, LocationGroupState groupStateOut, int count) {
        List<Location> locations = stockLocationRepository.findBy(
                count > 0 ? PageRequest.of(0, count) : Pageable.unpaged(),
                stockLocationGroupNames,
                groupStateIn,
                groupStateOut);
        if (locations.isEmpty()) {
            throw new NotFoundException("No locations found");
        }
        return locations;
    }
}
