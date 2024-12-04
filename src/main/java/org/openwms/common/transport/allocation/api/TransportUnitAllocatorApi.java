/*
 * Copyright 2005-2024 the original author or authors.
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
package org.openwms.common.transport.allocation.api;

import org.openwms.core.lang.Triple;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A TransportUnitAllocatorApi is an allocation strategy to allocate {@code TransportUnits} based on some search criteria.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifiers = "commonTransportUnitAllocatorApi", dismiss404 = true)
public interface TransportUnitAllocatorApi {

    /**
     * Allocate and pre-reserve {@code TransportUnits}. The pre-reservation is expected to be acknowledged as soon as a {@code TransportUnit}
     * is considered by the caller.
     *
     * @param searchAttributes The attributes to search TransportUnits for
     * @param sourceLocationGroupNames A list of LocationGroup names to search the material in
     * @return An array of available Allocations
     */
    @PostMapping(value = "/allocation/generic")
    List<AllocationVO> allocate(
            @RequestBody List<Triple<String, Object, Class<?>>> searchAttributes,
            @RequestParam(value = "sourceLocationGroupNames", required = false) List<String> sourceLocationGroupNames);
}
