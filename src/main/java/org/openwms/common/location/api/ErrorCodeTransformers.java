/*
 * Copyright 2005-2019 the original author or authors.
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

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

/**
 * A ErrorCodeTransformers is a collection of transformer implementations that can transform between an {@code ErrorCode} to arbitrary
 * states used within the domain model. The {@code ErrorCode} is a bitmap that needs to be decoded into the actual state used in the domain
 * model.
 *
 * @author Heiko Scherrer
 */
public interface ErrorCodeTransformers {

    /**
     * Decode the GroupStateIn from the {@code ErrorCode}.
     */
    @FunctionalInterface
    interface GroupStateIn {
        Optional<LocationGroupState> available(@NotEmpty String errorCode);
    }

    /**
     * Decode the GroupStateOut from the {@code ErrorCode}.
     */
    @FunctionalInterface
    interface GroupStateOut {
        Optional<LocationGroupState> available(@NotEmpty String errorCode);
    }

    /**
     * Decode the LocationStateIn from the {@code ErrorCode}.
     */
    @FunctionalInterface
    interface LocationStateIn {
        Optional<Boolean> available(@NotEmpty String errorCode);
    }

    /**
     * Decode the LocationStateOut from the {@code ErrorCode}.
     */
    @FunctionalInterface
    interface LocationStateOut {
        Optional<Boolean> available(@NotEmpty String errorCode);
    }
}
