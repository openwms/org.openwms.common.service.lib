/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.transport.allocation;

import org.ameba.http.MeasuredRestController;
import org.openwms.common.transport.allocation.api.AllocationVO;
import org.openwms.common.transport.allocation.spi.GenericAllocator;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.lang.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A AllocationController.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class AllocationController extends AbstractWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllocationController.class);
    private final AllocationMapper allocationMapper;
    private final GenericAllocator genericAllocator;

    AllocationController(AllocationMapper allocationMapper, GenericAllocator genericAllocator) {
        this.allocationMapper = allocationMapper;
        this.genericAllocator = genericAllocator;
    }

    @PostMapping(value = "/allocation/generic")
    public ResponseEntity<List<AllocationVO>> allocate(
            @RequestBody List<Triple<String, Object, Class<?>>> searchAttributes,
            @RequestParam(value = "sourceLocationGroupNames", required = false) List<String> sourceLocationGroupNames) {
        var allocations = genericAllocator.allocate(searchAttributes, sourceLocationGroupNames);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found [{}] number of possible Allocation sets in one of the LocationGroups [{}]", allocations.size(), sourceLocationGroupNames);
        }
        return ResponseEntity.ok(allocationMapper.convert(allocations));
    }
}
