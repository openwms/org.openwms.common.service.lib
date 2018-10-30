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
