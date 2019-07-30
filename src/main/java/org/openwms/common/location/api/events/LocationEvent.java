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
package org.openwms.common.location.api.events;

import org.openwms.core.event.RootApplicationEvent;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * A LocationEvent.
 *
 * @author Heiko Scherrer
 */
public class LocationEvent extends RootApplicationEvent implements Serializable {

    private LocationEventType type;

    /**
     * Create a new LocationEvent.
     *
     * @param source The event sender
     * @param type The event type
     */
    @ConstructorProperties({"source", "type"})
    public LocationEvent(Object source, LocationEventType type) {
        super(source);
        this.type = type;
    }

    public static LocationEvent boot() {
        return new LocationEvent("BOOT", LocationEventType.BOOT);
    }

    public LocationEventType getType() {
        return type;
    }

    public static LocationEvent of(Object obj, LocationEventType type) {
        return new LocationEvent(obj, type);
    }

    public enum LocationEventType {
        BOOT, CREATED, CHANGED, DELETED, STATE_CHANGE;
    }
}
