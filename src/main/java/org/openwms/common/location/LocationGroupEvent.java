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
package org.openwms.common.location;

import org.openwms.core.event.RootApplicationEvent;

/**
 * A LocationGroupEvent.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class LocationGroupEvent extends RootApplicationEvent {

    private LocationGroupEventType type;

    /**
     * Create a new RootApplicationEvent.
     *
     * @param source The event sender
     */
    private LocationGroupEvent(Object source, LocationGroupEventType type) {
        super(source);
        this.type = type;
    }

    public LocationGroupEventType getType() {
        return type;
    }

    public static LocationGroupEvent of(LocationGroup lg, LocationGroupEventType type) {
        return new LocationGroupEvent(lg, type);
    }

    public static enum LocationGroupEventType {
        CREATED, CHANGED, DELETED, STATE_CHANGE;
    }
}
