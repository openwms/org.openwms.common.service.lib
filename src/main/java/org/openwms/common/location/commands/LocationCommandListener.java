/*
 * Copyright 2005-2021 the original author or authors.
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
package org.openwms.common.location.commands;

import org.ameba.annotation.Measured;
import org.ameba.app.SpringProfiles;
import org.openwms.common.location.LocationService;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.ValidationGroups;
import org.openwms.common.location.api.commands.LocationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

import static org.ameba.system.ValidationUtil.validate;
import static org.openwms.common.location.api.LocationApiConstants.LOCATION_EMPTY;

/**
 * A LocationCommandListener.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
public class LocationCommandListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationCommandListener.class);
    private final Validator validator;
    private final LocationService locationService;

    public LocationCommandListener(Validator validator, LocationService locationService) {
        this.validator = validator;
        this.locationService = locationService;
    }

    @Measured
    @RabbitListener(queues = "${owms.commands.common.loc.queue-name}")
    public void onCommand(@Payload LocationCommand command) {
        switch(command.getType()) {
            case SET_LOCATION_EMPTY:
                validate(validator,command, ValidationGroups.SetLocationEmpty.class);
                LOGGER.debug("Got command to set a Location [{}] empty", command.getSource().getpKey());
                var errorCode = ErrorCodeVO.LOCK_STATE_IN_AND_OUT;
                errorCode.setPlcState(LOCATION_EMPTY);
                locationService.changeState(command.getSource().getpKey(), errorCode);
                break;
            default:
        }
    }
}
