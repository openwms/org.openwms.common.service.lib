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
package org.openwms.common.location;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.location.api.LocationGroupVO;
import org.openwms.common.location.api.messages.LocationGroupMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.openwms.common.CommonMessageCodes.LOCATION_GROUP_NOT_FOUND;

/**
 * A LocationGroupMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper
public abstract class LocationGroupMapper {

    private Translator translator;
    private LocationGroupService locationGroupService;

    @Autowired
    public void setTranslator(Translator translator) {
        this.translator = translator;
    }

    @Autowired
    public void setLocationGroupService(LocationGroupService locationGroupService) {
        this.locationGroupService = locationGroupService;
    }

    public LocationGroup convertFromName(String name) {
        if (name == null) {
            return null;
        }
        var locationGroupOpt = locationGroupService.findByName(name);
        if (locationGroupOpt.isEmpty()) {
            throw new NotFoundException(translator, LOCATION_GROUP_NOT_FOUND, new String[]{name}, name);
        }
        return locationGroupOpt.get();
    }

    @Mapping(target = "pKey", source = "eo.persistentKey")
    @Mapping(target = "accountId", source = "eo.account.identifier")
    @Mapping(target = "parent", source = "eo.parent.name")
    @Mapping(target = "groupStateIn", source = "eo.groupStateIn")
    @Mapping(target = "groupStateOut", source = "eo.groupStateOut")
    @Mapping(target = "children", source = "eo.locationGroups")
    @Mapping(target = "createDt", source = "eo.createDt")
    public abstract LocationGroupVO convertToVO(LocationGroup eo);

    @Mapping(target = "accountId", source = "eo.account.identifier")
    @Mapping(target = "parent", source = "eo.parent.name")
    @Mapping(target = "operationMode", source = "eo.operationMode")
    @Mapping(target = "incomingActive", expression = "java( eo.isInfeedAllowed() )")
    @Mapping(target = "outgoingActive", expression = "java( eo.isOutfeedAllowed() )")
    public abstract LocationGroupMO convertToMO(LocationGroup eo);

    public abstract List<LocationGroupVO> convertToVO(List<LocationGroup> eo);
}
