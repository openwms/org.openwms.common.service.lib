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
package org.openwms.common.location.api.events;

import org.openwms.core.event.RootApplicationEvent;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A LocationGroupEvent.
 *
 * @author Heiko Scherrer
 */
public class LocationGroupEvent extends RootApplicationEvent implements Serializable {

    private LocationGroupEventType type;

    /**
     * Create a new RootApplicationEvent.
     *
     * @param source The event sender
     * @param type The event type
     */
    public LocationGroupEvent(@NotNull Object source, @NotNull LocationGroupEventType type) {
        super(assertThis(source, type));
        this.type = type;
    }

    private static Object assertThis(Object source, LocationGroupEventType type) {
        Assert.notNull(source, "source is null");
        Assert.notNull(type, "type is null");
        return source;
    }

    public static LocationGroupEvent boot() {
        return new LocationGroupEvent("BOOT", LocationGroupEventType.BOOT);
    }

    public LocationGroupEventType getType() {
        return type;
    }

    public static LocationGroupEvent of(Object obj, LocationGroupEventType type) {
        return new LocationGroupEvent(obj, type);
    }

    public enum LocationGroupEventType {
        BOOT, CREATED, CHANGED, DELETED, STATE_CHANGE;
    }
}
