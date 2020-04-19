/*
 * Copyright 2005-2020 the original author or authors.
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

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A TargetApi offers operations on Targets (i.e. resources), like locking and releasing a resource.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "targetApi", decode404 = true)
public interface TargetApi {

    /**
     * Change the current {@code mode} a {@code Target}, identified by {@code targetBK} operates in.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     * @param type The type of lock to apply to the Target
     * @param mode The mode to apply to the Targets lock
     */
    @PostMapping(value = LocationApiConstants.API_TARGETS + "/{targetBK}", params = {"type", "mode", "op=change-state"})
    void changeState(
            @PathVariable("targetBK") String targetBK,
            @RequestParam("type") LockType type,
            @RequestParam("mode") LockMode mode
    );

    /**
     * Lock the {@code Target} identified by {@code targetBK}.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     * @param reallocation If {@literal true} open outfeed orders will be re-allocated
     */
    @PostMapping(value = LocationApiConstants.API_TARGETS + "/{targetBK}", params = {"op=lock"})
    void lock(
            @PathVariable("targetBK") String targetBK,
            @RequestParam("reallocation") Boolean reallocation
    );

    /**
     * Unlock or release the {@code Target} identified by {@code targetBK}.
     *
     * @param targetBK The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     */
    @PostMapping(value = LocationApiConstants.API_TARGETS + "/{targetBK}", params = {"op=unlock"})
    void release(
            @PathVariable("targetBK") String targetBK
    );
}
