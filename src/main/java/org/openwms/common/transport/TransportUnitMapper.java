/*
 * Copyright 2005-2023 the original author or authors.
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
package org.openwms.common.transport;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.openwms.common.location.LocationMapper;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A TransportUnitMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(uses = {LocationMapper.class, TransportUnitTypeMapper.class}, builder = @Builder(disableBuilder = true))
public abstract class TransportUnitMapper {

    @Autowired
    protected BarcodeGenerator barcodeGenerator;

    @Mapping(target = "parent", source = "source.parent", ignore = true)
    @Mapping(target = "inventoryDate", source = "source.inventoryDate", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void copy(TransportUnit source, @MappingTarget TransportUnit target);

    @Mapping(target = "persistentKey", source = "vo.pKey")
    @Mapping(target = "barcode", expression = "java( barcodeGenerator.convert(vo.getBarcode()) )")
    public abstract TransportUnit convert(TransportUnitVO vo);

    @Mapping(target = "pKey", source = "eo.persistentKey")
    @Mapping(target = "barcode", source = "eo.barcode.value")
    @Mapping(target = "transportUnitType", source = "eo.transportUnitType.type")
    @Mapping(target = "createDate", source = "eo.createDt")
    public abstract TransportUnitVO convertToVO(TransportUnit eo);

    public abstract List<TransportUnitVO> convertToVO(List<TransportUnit> eo);

    @Mapping(target = "pKey", source = "eo.persistentKey")
    @Mapping(target = "barcode", expression = "java( eo.getBarcode().getValue() )")
    @Mapping(target = "actualLocation", source = "actualLocation")
    @Mapping(target = "targetLocation", source = "targetLocation")
    @Mapping(target = "parent", expression = "java( eo.getParent() == null ? null : eo.getParent().getBarcode().getValue() )")
    public abstract TransportUnitMO convertToMO(TransportUnit eo);
}
