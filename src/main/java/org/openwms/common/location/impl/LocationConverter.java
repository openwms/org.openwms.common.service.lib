/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.location.impl;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.dozer.DozerConverter;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A LocationConverter.
 *
 * @author Heiko Scherrer
 */
@Configurable
public class LocationConverter extends DozerConverter<String, Location> {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private Translator translator;

    public LocationConverter() {
        super(String.class, Location.class);
    }

    /**
     * Converts the source field to the destination field and return the resulting destination value.
     *
     * @param source the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    @Override
    public Location convertTo(String source, Location destination) {
        return locationRepository.findByLocationId(LocationPK.fromString(source)).orElseThrow(()->new NotFoundException(translator, CommonMessageCodes.LOCATION_NOT_FOUND, new String[]{source}, source));
    }

    /**
     * Converts the source field to the destination field and return the resulting destination value
     *
     * @param source the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    @Override
    public String convertFrom(Location source, String destination) {
        return source.getLocationId().toString();
    }
}
