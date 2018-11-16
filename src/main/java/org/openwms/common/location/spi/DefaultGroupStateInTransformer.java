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
package org.openwms.common.location.spi;

import org.openwms.common.location.LocationGroupState;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * A DefaultGroupStateInTransformer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Order(5)
@Component
class DefaultGroupStateInTransformer implements ErrorCodeTransformers.GroupStateIn {

    @Override
    public LocationGroupState transform(String errorCode) {
        Assert.hasText(errorCode, "ErrorCode must be applied");
        // A Zero in the errorCode means no errors
        if (errorCode.charAt(errorCode.length()-1) == 48) {
            return LocationGroupState.AVAILABLE;
        };
        return LocationGroupState.NOT_AVAILABLE;
    }
}
