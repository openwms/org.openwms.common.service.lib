/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2018 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.location.spi;

import org.openwms.common.location.LocationGroupState;
import org.openwms.common.location.api.ErrorCodeTransformers;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * A DefaultGroupStateOutTransformer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Order(5)
@Component
class DefaultGroupStateOutTransformer implements ErrorCodeTransformers.GroupStateOut {

    @Override
    public LocationGroupState transform(String errorCode) {
        Assert.hasText(errorCode, "ErrorCode must be applied");
        // A Zero in the errorCode means no errors
        if (errorCode.charAt(errorCode.length()-2) == 48) {
            return LocationGroupState.AVAILABLE;
        };
        return LocationGroupState.NOT_AVAILABLE;
    }
}
