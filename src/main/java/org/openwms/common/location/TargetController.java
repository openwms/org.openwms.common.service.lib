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
package org.openwms.common.location;

import org.ameba.exception.NotFoundException;
import org.openwms.common.CommonConstants;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupMode;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LockType;
import org.openwms.common.location.api.OperationMode;
import org.openwms.common.location.api.events.TargetEvent;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.BiConsumer;

import static java.lang.String.format;

/**
 * A TargetController represents the REST API to handle the state and availability of {@code Target}s.
 *
 * @author Heiko Scherrer
 */
@RestController
class TargetController extends AbstractWebController {

    private final LocationService locationService;
    private final LocationGroupService locationGroupService;
    private final ApplicationContext ctx;

    TargetController(LocationService locationService, LocationGroupService locationGroupService, ApplicationContext ctx) {
        this.locationService = locationService;
        this.locationGroupService = locationGroupService;
        this.ctx = ctx;
    }

    /**
     * Change the current {@code mode} a {@code Target}, identified by {@code targetBK} operates in.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     * @param type The type of lock to apply to the Target
     * @param mode The mode to apply to the Targets lock
     */
    @PostMapping(path = CommonConstants.API_TARGETS + "/{targetBK}", params = {"type", "mode", "op=lock"})
    void changeState(
            @PathVariable("targetBK") String targetBK,
            @RequestParam("type") LockType type,
            @RequestParam("mode") OperationMode mode
    ) {

            if (LocationPK.isValid(targetBK)) {

                Location location = locationService.findByLocationId(targetBK).orElseThrow(() -> new NotFoundException(format("A Location as Target with LocationId [%s] does not exist", targetBK)));
                switch(type) {
                    case ALLOCATION_LOCK:
                        changeLocation(
                                mode,
                                location,
                                (l, code) -> locationService.changeState(l.getPersistentKey(), code)
                        );
                        break;
                    case OPERATION_LOCK:
                        throw new UnsupportedOperationException("Changing the operation mode of Locations is currently not supported in the API");
                    default:
                        throw new IllegalArgumentException(format("The Lock Type [%s] is not supported", type));
                }
            } else {

                Optional<LocationGroup> optLG = locationGroupService.findByName(targetBK);
                if (optLG.isPresent()) {

                    switch(type) {
                        case ALLOCATION_LOCK:
                            changeLocationGroupState(
                                    mode,
                                    optLG.get(),
                                    (lg, states) -> locationGroupService.changeGroupState(lg.getPersistentKey(), states[0], states[1])
                            );
                            break;
                        case OPERATION_LOCK:
                            changeLocationGroupMode(
                                    mode,
                                    optLG.get(),
                                    (lg, m) -> locationGroupService.changeOperationMode(lg.getName(), m)
                            );
                            break;
                        default:
                            throw new IllegalArgumentException(format("The Lock Type [%s] is not supported", type));
                    }
                } else {
                    throw new NotFoundException(format("The Target with name [%s] is neither a Location nor a LocationGroup. Other types of Targets are currently not supported", targetBK));
                }
            }
    }

    private void changeLocation(OperationMode mode, Target target, BiConsumer<Target, ErrorCodeVO> fnc) {
        switch(mode) {
            case IN:
                fnc.accept(target, ErrorCodeVO.UNLOCK_STATE_IN);
                break;
            case OUT:
                fnc.accept(target, ErrorCodeVO.UNLOCK_STATE_OUT);
                break;
            case IN_AND_OUT:
                fnc.accept(target, ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT);
                break;
            case BLOCKED:
                fnc.accept(target, ErrorCodeVO.LOCK_STATE_IN_AND_OUT);
                break;
            default:
                throw new IllegalArgumentException(format("The OperationMode [%s] is not supported", mode));
        }
    }

    private void changeLocationGroupState(OperationMode mode, Target target, BiConsumer<Target, LocationGroupState[]> fnc) {
        switch(mode) {
            case IN:
                fnc.accept(target, new LocationGroupState[]{LocationGroupState.AVAILABLE, LocationGroupState.NOT_AVAILABLE});
                break;
            case OUT:
                fnc.accept(target, new LocationGroupState[]{LocationGroupState.NOT_AVAILABLE, LocationGroupState.AVAILABLE});
                break;
            case IN_AND_OUT:
                fnc.accept(target, new LocationGroupState[]{LocationGroupState.AVAILABLE, LocationGroupState.AVAILABLE});
                break;
            case BLOCKED:
                fnc.accept(target, new LocationGroupState[]{LocationGroupState.NOT_AVAILABLE, LocationGroupState.NOT_AVAILABLE});
                break;
            default:
                throw new IllegalArgumentException(format("The OperationMode [%s] is not supported", mode));
        }
    }

    private void changeLocationGroupMode(OperationMode mode, LocationGroup target, BiConsumer<LocationGroup, String> fnc) {
        switch(mode) {
            case IN:
                fnc.accept(target, LocationGroupMode.INFEED);
                break;
            case OUT:
                fnc.accept(target, LocationGroupMode.OUTFEED);
                break;
            case IN_AND_OUT:
                fnc.accept(target, LocationGroupMode.INFEED_AND_OUTFEED);
                break;
            case BLOCKED:
                fnc.accept(target, LocationGroupMode.NO_OPERATION);
                break;
            default:
                throw new IllegalArgumentException(format("The OperationMode [%s] is not supported", mode));
        }
    }

    /**
     * Lock the {@code Target} identified by {@code targetBK}.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     * @param reAllocation If {@literal true} open outfeed orders will be re-allocated
     */
    @PostMapping(path = CommonConstants.API_TARGETS + "/{targetBK}", params = {"op=lock"})
    void lock(
            @PathVariable("targetBK") String targetBK,
            @RequestParam(value = "reallocation", required = false) Boolean reAllocation
    ) {
        if (LocationPK.isValid(targetBK)) {
            Location location = locationService.findByLocationId(targetBK).orElseThrow(() -> new NotFoundException(format("A Location as Target with LocationId [%s] does not exist", targetBK)));

            // Okay we handle a Location as Target
            locationService.changeState(location.getPersistentKey(), ErrorCodeVO.LOCK_STATE_IN_AND_OUT);
            raiseEvent(targetBK, reAllocation, OperationMode.BLOCKED);
            return;
        }

        Optional<LocationGroup> optLG = locationGroupService.findByName(targetBK);
        if (optLG.isPresent()) {

            // The Target is a LocationGroup
            locationGroupService.changeGroupState(optLG.get().getPersistentKey(), LocationGroupState.NOT_AVAILABLE, LocationGroupState.NOT_AVAILABLE);
            raiseEvent(targetBK, reAllocation, OperationMode.BLOCKED);
            return;
        }

        throw new NotFoundException(format("The Target with name [%s] is neither a Location nor a LocationGroup. Other types of Targets are currently not supported", targetBK));
    }

    /**
     * Unlock or release the {@code Target} identified by {@code targetBK}.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = CommonConstants.API_TARGETS + "/{targetBK}", params = {"op=unlock"})
    void release(
            @PathVariable("targetBK") String targetBK
    ) {
        if (LocationPK.isValid(targetBK)) {
            Location location = locationService.findByLocationId(targetBK).orElseThrow(() -> new NotFoundException(format("A Location as Target with LocationId [%s] does not exist", targetBK)));

            // Okay we handle a Location as Target
            locationService.changeState(location.getPersistentKey(), ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT);
            raiseEvent(targetBK, null, OperationMode.IN_AND_OUT);
            return;
        }

        Optional<LocationGroup> optLG = locationGroupService.findByName(targetBK);
        if (optLG.isPresent()) {

            // The Target is a LocationGroup
            locationGroupService.changeGroupState(optLG.get().getPersistentKey(), LocationGroupState.AVAILABLE, LocationGroupState.AVAILABLE);
            raiseEvent(targetBK, null, OperationMode.IN_AND_OUT);
            return;
        }

        throw new NotFoundException(format("The Target with name [%s] is neither a Location nor a LocationGroup. Other types of Targets are currently not supported", targetBK));
    }

    private void raiseEvent(String targetBK, Boolean reAllocation, OperationMode mode) {
        ctx.publishEvent(
                TargetEvent
                        .newBuilder()
                        .targetBK(targetBK)
                        .lockType(LockType.PERMANENT_LOCK)
                        .operationMode(mode)
                        .reAllocation(reAllocation)
                        .build()
        );
    }
}