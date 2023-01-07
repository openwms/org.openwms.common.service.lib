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
package org.openwms.common.location;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.account.impl.AccountMapper;
import org.openwms.common.location.api.LocationTypeVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.openwms.common.CommonMessageCodes.LOCATION_TYPE_NOT_FOUND;

/**
 * A LocationTypeMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(uses = {AccountMapper.class})
public abstract class LocationTypeMapper {

    @Autowired
    private LocationTypeService locationTypeService;
    @Autowired
    private Translator translator;

    public LocationType convertFromString(String type) {
        if (type == null) {
            return null;
        }
        var locationTypeOpt = locationTypeService.findByTypeName(type);
        if (locationTypeOpt.isEmpty()) {
            throw new NotFoundException(translator, LOCATION_TYPE_NOT_FOUND, new String[]{type}, type);
        }
        return locationTypeOpt.get();
    }

    @Mapping(target = "pKey", source = "eo.persistentKey")
    public abstract LocationTypeVO convertToVO(LocationType eo);

    public abstract List<LocationTypeVO> convertToVO(List<LocationType> eo);
}
