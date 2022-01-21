/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common;

import org.openwms.common.account.AccountController;
import org.openwms.common.location.LocationController;
import org.openwms.common.location.LocationGroupController;
import org.openwms.common.transport.TransportUnitController;
import org.openwms.common.transport.TransportUnitTypeController;
import org.openwms.core.http.Index;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A IndexController serves the root {@link Index} resource via REST.
 *
 * @author Heiko Scherrer
 */
@RestController("commonIndexController")
class IndexController {

    @GetMapping("/index")
    public ResponseEntity<Index> getIndex() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(AccountController.class).index()).withRel("account-index"),
                        linkTo(methodOn(LocationController.class).index()).withRel("location-index"),
                        linkTo(methodOn(LocationGroupController.class).index()).withRel("location-group-index"),
                        linkTo(methodOn(TransportUnitTypeController.class).index()).withRel("transport-unit-type-index"),
                        linkTo(methodOn(TransportUnitController.class).index()).withRel("transport-unit-index")
                )
        );
    }
}
