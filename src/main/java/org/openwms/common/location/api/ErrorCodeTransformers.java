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

import java.util.Optional;

/**
 * A ErrorCodeTransformer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface ErrorCodeTransformers {

    @FunctionalInterface
    interface GroupStateIn {
        Optional<LocationGroupState> available(String errorCode);
    }

    @FunctionalInterface
    interface GroupStateOut {
        Optional<LocationGroupState> available(String errorCode);
    }

    @FunctionalInterface
    interface LocationStateIn {
        Optional<Boolean> available(String errorCode);
    }

    @FunctionalInterface
    interface LocationStateOut {
        Optional<Boolean> available(String errorCode);
    }
}
