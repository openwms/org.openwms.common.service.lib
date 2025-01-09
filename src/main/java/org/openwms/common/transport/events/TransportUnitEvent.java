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
package org.openwms.common.transport.events;

import org.openwms.common.location.Location;
import org.openwms.common.transport.TransportUnit;
import org.openwms.core.event.RootApplicationEvent;

/**
 * A TransportUnitEvent.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitEvent extends RootApplicationEvent {

    private TransportUnitEventType type;
    private Location actualLocation;
    private Location previousLocation;

    private TransportUnitEvent(Object source, TransportUnitEventType type) {
        super(source);
        this.type = type;
    }

    private TransportUnitEvent(Builder builder) {
        super(builder.source);
        type = builder.type;
        actualLocation = builder.actualLocation;
        previousLocation = builder.previousLocation;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public TransportUnitEventType getType() {
        return type;
    }

    public Location getActualLocation() {
        return actualLocation;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public static TransportUnitEvent of(TransportUnit tu, TransportUnitEventType type) {
        return new TransportUnitEvent(tu, type);
    }

    public enum TransportUnitEventType {
        CREATED, CHANGED, DELETED, MOVED, STATE_CHANGE;
    }

    public static final class Builder {
        private Object source;
        private TransportUnitEventType type;
        private Location previousLocation;
        private Location actualLocation;

        private Builder() {
        }

        public Builder tu(TransportUnit val) {
            source = val;
            return this;
        }

        public Builder type(TransportUnitEventType val) {
            type = val;
            return this;
        }

        public Builder previousLocation(Location val) {
            previousLocation = val;
            return this;
        }

        public Builder actualLocation(Location val) {
            actualLocation = val;
            return this;
        }

        public TransportUnitEvent build() {
            if (type == TransportUnitEventType.MOVED && actualLocation == null) {
                throw new IllegalArgumentException("TU MOVED events must contain the actualLocation");
            }
            return new TransportUnitEvent(this);
        }
    }
}
