/*
 * Copyright 2005-2021 the original author or authors.
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
package org.openwms.common.transport.api;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT_TYPES;

/**
 * A TransportUnitTypeApi is the public REST API to manage {@code TransportUnitTypes}. It is implemented by a {@code Feign} client stub that
 * has caching enabled.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "transportUnitTypeApi")
public interface TransportUnitTypeApi {

    /**
     * Find and return a {@code TransportUnitType} identified by its {@code type}.
     *
     * @param type The unique identifier
     * @return The instance or the implementation may return a 404-Not Found
     */
    @GetMapping(value = API_TRANSPORT_UNIT_TYPES, params = {"type"})
    @ResponseBody
    @Cacheable("transportUnitTypes")
    TransportUnitTypeVO findTransportUnitType(
            @RequestParam("type") String type
    );

    /**
     * Find and return all {@code TransportUnitType}s.
     *
     * @return All instances or an empty list, never {@literal null}
     */
    @GetMapping(API_TRANSPORT_UNIT_TYPES)
    @ResponseBody
    @Cacheable("transportUnitTypes")
    List<TransportUnitTypeVO> findTransportUnitTypes();

}
