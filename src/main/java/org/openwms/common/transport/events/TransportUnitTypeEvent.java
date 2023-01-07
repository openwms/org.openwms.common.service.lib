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
package org.openwms.common.transport.events;

import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.core.event.RootApplicationEvent;

/**
 * A TransportUnitTypeEvent.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitTypeEvent extends RootApplicationEvent {

    private TransportUnitTypeEventType type;

    private TransportUnitTypeEvent(Object source, TransportUnitTypeEventType type) {
        super(source);
        this.type = type;
    }

    private TransportUnitTypeEvent(Builder builder) {
        super(builder.source);
        type = builder.type;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public TransportUnitTypeEventType getType() {
        return type;
    }

    public static TransportUnitTypeEvent of(TransportUnit tu, TransportUnitTypeEventType type) {
        return new TransportUnitTypeEvent(tu, type);
    }

    public enum TransportUnitTypeEventType {
        CREATED, CHANGED, DELETED;
    }

    public static final class Builder {
        private Object source;
        private TransportUnitTypeEventType type;

        private Builder() {
        }

        public Builder tut(TransportUnitType val) {
            source = val;
            return this;
        }

        public Builder type(TransportUnitTypeEventType val) {
            type = val;
            return this;
        }

        public TransportUnitTypeEvent build() {
            return new TransportUnitTypeEvent(this);
        }
    }
}
