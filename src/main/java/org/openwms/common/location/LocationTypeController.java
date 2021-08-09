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
package org.openwms.common.location;

import org.ameba.http.MeasuredRestController;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.api.LocationTypeVO;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION_TYPES;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A LocationTypeController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@MeasuredRestController
public class LocationTypeController extends AbstractWebController {

    private final LocationTypeService locationTypeService;
    private final BeanMapper mapper;

    LocationTypeController(LocationTypeService locationTypeService, BeanMapper mapper) {
        this.locationTypeService = locationTypeService;
        this.mapper = mapper;
    }

    @GetMapping(value = API_LOCATION_TYPES)
    public ResponseEntity<List<LocationTypeVO>> findAll() {
        return ResponseEntity.ok(mapper.map(locationTypeService.findAll(), LocationTypeVO.class));
    }

    @GetMapping(API_LOCATION_TYPES + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
            new Index(
                linkTo(methodOn(LocationTypeController.class).findAll()).withRel("locationType-findall")
            )
        );
    }
}
