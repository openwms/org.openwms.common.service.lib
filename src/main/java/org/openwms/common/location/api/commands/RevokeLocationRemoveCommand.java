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
package org.openwms.common.location.api.commands;

import java.io.Serializable;

/**
 * A RevokeLocationRemoveCommand.
 *
 * @author Heiko Scherrer
 */
public class RevokeLocationRemoveCommand implements Serializable {

    private String pKey;

    public RevokeLocationRemoveCommand(String pKey) {
        this.pKey = pKey;
    }

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }
}
