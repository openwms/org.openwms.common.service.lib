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
package org.openwms.common.location.events;

import jakarta.validation.constraints.NotBlank;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * A DeletionFailedEvent.
 *
 * @author Heiko Scherrer
 */
public class DeletionFailedEvent extends ApplicationEvent implements Serializable {

    public DeletionFailedEvent(@NotBlank String pKey) {
        super(pKey);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }
}
