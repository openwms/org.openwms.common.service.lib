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
package org.openwms.common.location.api;

import java.io.Serializable;

/**
 * A LockMode is an enumeration of possible modes a Target resource can be locked for.
 *
 * @author Heiko Scherrer
 */
public enum LockMode implements Serializable {

    /** Locked for inbound operations. */
    IN,

    /** Locked for outbound operations. */
    OUT,

    /** Locked for inbound and outbound operations. */
    IN_AND_OUT,

    /** Not locked for any operation. */
    NONE
}
