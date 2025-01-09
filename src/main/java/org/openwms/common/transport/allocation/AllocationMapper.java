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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.transport.allocation.api.AllocationVO;
import org.openwms.common.transport.allocation.spi.TransportUnitAllocation;

import java.util.List;

/**
 * A AllocationMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper
public interface AllocationMapper {

    @Mapping(target = "transportUnitBK", source = "entity.transportUnit.barcode.value")
    @Mapping(target = "actualErpCode", source = "entity.actualLocation.erpCode")
    @Mapping(target = "reservationId", source = "reservationId")
    AllocationVO convert(TransportUnitAllocation entity);

    List<AllocationVO> convert(List<TransportUnitAllocation> entities);
}
