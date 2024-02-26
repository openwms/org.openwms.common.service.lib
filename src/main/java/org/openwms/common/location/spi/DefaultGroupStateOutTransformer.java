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
package org.openwms.common.location.spi;

import org.openwms.common.location.api.ErrorCodeTransformers;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

/**
 * A DefaultGroupStateOutTransformer.
 *
 * @author Heiko Scherrer
 */
@Order(5)
@Component
class DefaultGroupStateOutTransformer implements ErrorCodeTransformers.GroupStateOut {

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocationGroupState> available(@NotEmpty String errorCode) {
        Assert.hasText(errorCode, "ErrorCode must be applied");
        if (errorCode.charAt(ErrorCodeVO.STATE_OUT_POSITION) == 42 /* '*' */) {
            return Optional.empty();
        }
        // A Zero in the errorCode means no errors
        return Optional.of(errorCode.charAt(ErrorCodeVO.STATE_OUT_POSITION) == 48 ? LocationGroupState.AVAILABLE : LocationGroupState.NOT_AVAILABLE);
    }
}
