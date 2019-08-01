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

import org.dozer.DozerConverter;
import org.openwms.common.location.api.LocationGroupState;

/**
 * A LocationGroupStateConverter.
 *
 * @author Heiko Scherrer
 */
public class LocationGroupStateConverter extends DozerConverter<Boolean, LocationGroupState> {


    public LocationGroupStateConverter() {
        super(Boolean.class, LocationGroupState.class);
    }

    /**
     * Converts the source field to the destination field and return the resulting destination value.
     *
     * @param source the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    @Override
    public LocationGroupState convertTo(Boolean source, LocationGroupState destination) {
        if (source == null) {
            return null;
        }
        return source ? LocationGroupState.AVAILABLE : LocationGroupState.NOT_AVAILABLE;
    }

    /**
     * Converts the source field to the destination field and return the resulting destination value
     *
     * @param source the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    @Override
    public Boolean convertFrom(LocationGroupState source, Boolean destination) {
        if (source == null) {
            return null;
        }
        return source == LocationGroupState.AVAILABLE;
    }
}
