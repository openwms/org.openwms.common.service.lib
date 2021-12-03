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
package org.openwms.common.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.account.impl.AccountMapper;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.location.api.messages.LocationMO;

import java.util.List;

/**
 * A LocationMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(uses = {AccountMapper.class, LocationTypeMapper.class, LocationGroupMapper.class})
public interface LocationMapper {

    @Mapping(target = "persistentKey", source = "pKey")
    @Mapping(target = "locationId", expression = "java( org.openwms.common.location.LocationPK.fromString(vo.getLocationId()) )")
    @Mapping(target = "locationType", source = "type")
    @Mapping(target = "account", source = "accountId")
    @Mapping(target = "locationGroup", source = "locationGroupName")
    @Mapping(target = "erpCode", source = "erpCode")
    @Mapping(target = "plcCode", source = "plcCode")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "incomingActive", source = "incomingActive", ignore = true)
    @Mapping(target = "outgoingActive", source = "outgoingActive", ignore = true)
    @Mapping(target = "plcState", source = "plcState")
    @Mapping(target = "stockZone", source = "stockZone")
    Location convertVO(LocationVO vo);

    @Mapping(target = "pKey", source = "eo.persistentKey")
    @Mapping(target = "accountId", source = "eo.account.identifier")
    @Mapping(target = "type", source = "eo.locationType.type")
    @Mapping(target = "locationId", expression = "java( eo.getLocationId().toString() )")
    @Mapping(target = "locationGroupName", source = "eo.locationGroup.name")
    @Mapping(target = "incomingActive", source = "eo.infeedActive")
    @Mapping(target = "outgoingActive", source = "eo.outfeedActive")
    LocationVO convertToVO(Location eo);

    List<LocationVO> convertToVO(List<Location> eo);

    @Mapping(target = "pKey", source = "eo.persistentKey")
    @Mapping(target = "accountId", source = "eo.account.identifier")
    @Mapping(target = "id", expression = "java( eo.getLocationId().toString() )")
    @Mapping(target = "incomingActive", source = "eo.infeedActive")
    @Mapping(target = "outgoingActive", source = "eo.outfeedActive")
    LocationMO convertToMO(Location eo);
}
