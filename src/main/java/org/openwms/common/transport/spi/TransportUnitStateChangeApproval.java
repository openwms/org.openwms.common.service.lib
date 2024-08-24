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

import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitState;

/**
 * A TransportUnitStateChangeApproval implementation validates a requested state change of a {@link TransportUnit} and can prevent it.
 *
 * @author Heiko Scherrer
 */
@FunctionalInterface
public interface TransportUnitStateChangeApproval {

    /**
     * Validate whether changing the state of the {@code transportUnit} into {@code newState} is permitted.
     *
     * @param transportUnit The TransportUnit to change the state
     * @param newState The new state to set
     * @throws NotApprovedException If not allowed to change
     */
    void approve(TransportUnit transportUnit, TransportUnitState newState) throws NotApprovedException;
}
