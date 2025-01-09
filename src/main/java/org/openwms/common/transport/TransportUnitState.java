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
package org.openwms.common.transport;

/**
 * A TransportUnitState defines a set of states for {@code TransportUnit}s.
 * 
 * @GlossaryTerm
 * @author <a href="mailto:russelltina@users.sourceforge.net">Tina Russell</a>
 * @author Heiko Scherrer
 * @see TransportUnit
 */
public enum TransportUnitState {

    /** The {@code TransportUnit} is available for operations. */
    AVAILABLE,

    /** The {@code TransportUnit} is planned for QC processing. */
    QUALITY_CHECK,

    /** The {@code TransportUnit} is blocked for operations without any particular reason. */
    BLOCKED
}