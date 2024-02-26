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
package org.openwms.common;

/**
 * A StateChangeException signals that the request to change the state of an entity was not allowed.
 *
 * @author Heiko Scherrer
 */
public class StateChangeException extends RuntimeException {

    /**
     * Create a new with a message text.
     *
     * @param message The message text
     */
    public StateChangeException(String message) {
        super(message);
    }
}
