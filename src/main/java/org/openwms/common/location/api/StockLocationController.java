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
package org.openwms.common.location.api;

import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.Location;
import org.openwms.common.location.stock.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A StockLocationController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@RestController("/stock")
class StockLocationController implements StockLocationApi {

    private final StockService stockService;
    private final BeanMapper mapper;

    StockLocationController(StockService stockService, BeanMapper mapper) {
        this.stockService = stockService;
        this.mapper = mapper;
    }

    @Override
    @GetMapping(params = {"stockLocationGroupNames", "count"})
    public List<LocationVO> findStockLocationSimple(@RequestParam("stockLocationGroupNames") List<String> stockLocationGroupNames, @RequestParam("count") int count) {
        List<Location> location = stockService.findNextAscending(stockLocationGroupNames, count);
        return mapper.map(location, LocationVO.class);
    }
}
