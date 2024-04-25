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
package org.openwms.common.location;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.openwms.common.account.impl.AccountMapper;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.location.api.messages.LocationMO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static java.lang.String.format;

/**
 * A LocationMapper.
 *
 * @author Heiko Scherrer
 */
@Validated
@Mapper(uses = {AccountMapper.class, LocationTypeMapper.class, LocationGroupMapper.class})
public interface LocationMapper {

    @Mapping(target = "persistentKey", source = "pKey")
    @Mapping(target = "locationId", expression = "java( org.openwms.common.location.LocationPK.fromString(vo.getLocationId()) )",
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "locationType", source = "type")
    @Mapping(target = "account", source = "accountId", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "locationGroup", source = "locationGroupName")
    @Mapping(target = "erpCode", source = "erpCode")
    @Mapping(target = "plcCode", source = "plcCode")
    @Mapping(target = "sortOrder", source = "sortOrder")
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

    default Location copyForUpdate(Location source, @NotNull Location target) {
        if ( source == null ) {
            return target;
        }

        if (!source.getLocationId().equals(target.getLocationId())) {
            throw new IllegalArgumentException(format("Not allowed to change the LocationId from [%s] to [%s]", source.getLocationId(),
                    target.getLocationId()));
        }

        var builder = Location.LocationBuilder.aLocation(target);

        builder.withAccount( source.getAccount() )
                .withPlcCode( source.getPlcCode() )
                .withErpCode( source.getErpCode() )
                .withDescription( source.getDescription() )
                .withSortOrder( source.getSortOrder() )
                .withStockZone( source.getStockZone() )
                .withLabels( source.getLabels() )
                .withNoMaxTransportUnits( source.getNoMaxTransportUnits() )
                .withMaximumWeight( source.getMaximumWeight() )
                .withLastMovement( source.getLastMovement() )
                .withLocationType( source.getLocationType() )
                .withLocationGroupCountingActive( source.isLocationGroupCountingActive() )
                .withConsideredInAllocation( source.isConsideredInAllocation() )
                .withLocationType( source.getLocationType() )
                .withGroup( source.getGroup() )
                .withClassification( source.getClassification() )
                .withLocationGroup( source.getLocationGroup() )
                .withMessages( source.getMessages() );

        return builder.build();
    }
}
