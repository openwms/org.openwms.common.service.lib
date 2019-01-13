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
package org.openwms.common.units;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A MeasurableVO.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_cType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Piece.class, name = "Piece"),
        @JsonSubTypes.Type(value = Weight.class, name = "Weight")})
public interface MeasurableVO<V extends Number, E extends MeasurableVO<V, E, T>, T extends BaseUnitVO<T>>{

    /**
     * Returns the type of {@code Measurable}.
     *
     * @return The {@code Measurable}'s type
     */
    T getUnitType();

    /**
     * Get the magnitude of this {@code Measurable}.
     *
     * @return the magnitude
     */
    V getMagnitude();
}
