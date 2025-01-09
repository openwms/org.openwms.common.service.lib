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
package org.openwms.common.location.api;

/**
 * A LockType defines the possible types of locks a {@code Target} can be set to.
 *
 * @author Heiko Scherrer
 */
public enum LockType {

    /** If the {@code Target} should not be considered in allocation (Zuteilung) the {@code ALLOCATION_LOCK} is set. */
    ALLOCATION_LOCK,

    /**
     * If the {@code Target} should be locked for any further operation, but may still be available for allocation,
     * then the {@code OPERATION_LOCK} is set.
     */
    OPERATION_LOCK,

    /** If the {@code Target} should be locked permanently for any further operation, then the {@code OPERATION_LOCK} is set. */
    PERMANENT_LOCK
}
