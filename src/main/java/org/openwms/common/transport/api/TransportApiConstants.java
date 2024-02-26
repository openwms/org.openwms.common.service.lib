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
package org.openwms.common.transport.api;

/**
 * A TransportUnitApiConstants.
 *
 * @author Heiko Scherrer
 */
public final class TransportApiConstants {

    /** API version. */
    public static final String API_VERSION = "v1";
    /** API root to hit TransportUnits (plural). */
    public static final String API_TRANSPORT_UNITS = "/" + API_VERSION + "/transport-units";
    /** API root to hit a TransportUnit. */
    public static final String API_TRANSPORT_UNIT = "/" + API_VERSION + "/transport-unit";
    /** API root to hit a TransportUnitType. */
    public static final String API_TRANSPORT_UNIT_TYPE = "/" + API_VERSION + "/transport-unit-type";
    /** API root to hit TransportUnitTypes (plural). */
    public static final String API_TRANSPORT_UNIT_TYPES = "/" + API_VERSION + "/transport-unit-types";

    private TransportApiConstants() {}
}
