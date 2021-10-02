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
package org.openwms.common.location.api.commands;

import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.core.event.RootApplicationEvent;

import javax.validation.Valid;

/**
 * A LocationCommand.
 *
 * @author Heiko Scherrer
 */
public class LocationCommand extends RootApplicationEvent {

    private Type type;
    private @Valid LocationMO location;

    public LocationCommand(Type type, LocationMO location) {
        super(location);
        this.type = type;
        this.location = location;
    }

    public static enum Type {
        SET_LOCATION_EMPTY
    }

    @Override
    public LocationMO getSource() {
        return (LocationMO) super.getSource();
    }

    public Type getType() {
        return type;
    }
}
