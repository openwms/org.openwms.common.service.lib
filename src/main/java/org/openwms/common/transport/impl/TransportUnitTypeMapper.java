/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common.transport.impl;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.api.TransportUnitTypeVO;
import org.openwms.common.transport.api.messages.TransportUnitTypeMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A TransportUnitTypeMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper
public abstract class TransportUnitTypeMapper {

    @Autowired
    private TransportUnitTypeRepository repository;

    @Mapping(target = "persistentKey", source = "vo.pKey")
    public abstract TransportUnitType convert(TransportUnitTypeVO vo);

    public abstract TransportUnitTypeMO convertToMO(TransportUnitType eo);

    @Mapping(target = "pKey", source = "eo.persistentKey")
    public abstract TransportUnitTypeVO convertToVO(TransportUnitType eo);

    public abstract List<TransportUnitTypeVO> convertToVO(List<TransportUnitType> eo);

    public TransportUnitType convert(String type) {
        return repository.findByType(type).orElseGet(() -> TransportUnitType.newBuilder(type).build());
    }
}
