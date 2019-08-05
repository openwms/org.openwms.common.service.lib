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
package org.openwms.common.location.api;

/**
 * A LocationGroupMode defines possible operation modes a {@code LocationGroup} can be operated. This is realized as a
 * Java class type instead of an enumeration type because it is meant to be extended by custom values. An LocationGroupMode is
 * different from the {@code LocationGroupState} where affects the actual operation of the assigned Subsystem - like a Crane.
 * A Crane could still be considered in infeed or outfeed allocation but is blocked for physical operations with the
 * {@code LocationGroupMode}.
 * 
 * @GlossaryTerm
 * @author Heiko Scherrer
 */
public class LocationGroupMode {

    /** The subsystem connected to the {@code LocationGroup} is able to drive Infeed operations. */
    public static final String INFEED = "INFEED";
    /** The subsystem connected to the {@code LocationGroup} is able to drive Outfeed operations. */
    public static final String OUTFEED = "OUTFEED";
    /** The subsystem connected to the {@code LocationGroup} is able to drive Infeed and Outfeed operations. */
    public static final String INFEED_AND_OUTFEED = "INFEED_AND_OUTFEED";
    /** The subsystem connected to the {@code LocationGroup} is blocked for any operations. */
    public static final String BLOCKED = "BLOCKED";

    // Not meant to be instantiated
    private LocationGroupMode() {
    }
}