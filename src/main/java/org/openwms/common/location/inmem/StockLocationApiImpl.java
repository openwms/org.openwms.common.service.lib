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
package org.openwms.common.location.inmem;

import org.ameba.annotation.TxService;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.Location;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.location.api.StockLocationApi;
import org.openwms.common.location.stock.StockService;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * A StockLocationApiImpl is a Spring managed transactional Service that is activated in
 * case of non-microservice deployments when the Spring Profile INMEM is activated.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("INMEM")
@TxService
class StockLocationApiImpl implements StockLocationApi {

    private final StockService stockService;
    private final BeanMapper mapper;

    StockLocationApiImpl(StockService stockService, BeanMapper mapper) {
        this.stockService = stockService;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocationVO> findAvailableStockLocations(List<String> stockLocationGroupNames, LocationGroupState groupStateIn, LocationGroupState groupStateOut, int count) {
        List<Location> locations = stockService.findAvailableStockLocations(stockLocationGroupNames, groupStateIn, groupStateOut, count);
        return mapper.map(locations, LocationVO.class);
    }
}
