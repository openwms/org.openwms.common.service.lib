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
package org.openwms.common.transport.spi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.common.location.Location;
import org.openwms.common.transport.TransportUnit;

/**
 * A TransportUnitMoveApproval implementation validates a request to move a {@link TransportUnit} and can prevent it.
 *
 * @author Heiko Scherrer
 */
@FunctionalInterface
public interface TransportUnitMoveApproval {

    /**
     * Approve that the requested move of the {@code transportUnit} to the {@code newLocation} is permitted.
     *
     * @param transportUnit The TransportUnit to move
     * @param newLocation The new location to move to
     * @throws NotApprovedException If not allowed to move
     */
    void approve(@NotNull TransportUnit transportUnit, @NotBlank Location newLocation) throws NotApprovedException;
}
