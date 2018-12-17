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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A StockLocationApi.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@FeignClient(name = "common-service", qualifier = "stockLocationApi", decode404 = false)
public interface StockLocationApi {

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
    @Cacheable("locations")
    @GetMapping(value = "/stock", params = {"stockLocationGroupNames", "count"})
    List<LocationVO> findAvailableStockLocations(@RequestParam("stockLocationGroupNames") List<String> stockLocationGroupNames, @RequestParam(value = "groupStateIn", required = false) LocationGroupState groupStateIn, @RequestParam(value = "groupStateOut", required = false) LocationGroupState groupStateOut, @RequestParam("count") int count);
}
