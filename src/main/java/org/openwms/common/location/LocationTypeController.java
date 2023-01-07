/*
 * Copyright 2005-2023 the original author or authors.
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
import org.openwms.common.location.api.LocationTypeVO;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final LocationTypeMapper mapper;

    LocationTypeController(LocationTypeService locationTypeService, LocationTypeMapper mapper) {
        this.locationTypeService = locationTypeService;
        this.mapper = mapper;
    }

    @GetMapping(value = API_LOCATION_TYPES + "/{pKey}", produces = LocationTypeVO.MEDIA_TYPE)
    public ResponseEntity<LocationTypeVO> findByPKey(@PathVariable("pKey") String pKey) {
        var locationType = locationTypeService.findByPKey(pKey);
        return ResponseEntity.ok(convertAndLinks(locationType));
    }

    @GetMapping(value = API_LOCATION_TYPES, params = "typeName", produces = LocationTypeVO.MEDIA_TYPE)
    public ResponseEntity<LocationTypeVO> findByName(@RequestParam("typeName") String typeName) {
        var locationTypeOpt = locationTypeService.findByTypeName(typeName);
        if (locationTypeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertAndLinks(locationTypeOpt.get()));
    }

    @GetMapping(value = API_LOCATION_TYPES, produces = LocationTypeVO.MEDIA_TYPE)
    public ResponseEntity<List<LocationTypeVO>> findAll() {
        return ResponseEntity.ok(convertAndLinks(locationTypeService.findAll()));
    }

    @GetMapping(API_LOCATION_TYPES + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
            new Index(
                linkTo(methodOn(LocationTypeController.class).findByPKey("pKey")).withRel("location-types-findbypkey"),
                linkTo(methodOn(LocationTypeController.class).findByName("typeName")).withRel("location-types-findbytypename"),
                linkTo(methodOn(LocationTypeController.class).findAll()).withRel("location-types-findall")
            )
        );
    }

    private LocationTypeVO convertAndLinks(LocationType resource) {
        return addSelfLink(
                mapper.convertToVO(resource)
        );
    }

    private List<LocationTypeVO> convertAndLinks(List<LocationType> resources) {
        return resources.stream()
                .map(mapper::convertToVO)
                .map(this::addSelfLink)
                .toList();
    }

    private LocationTypeVO addSelfLink(LocationTypeVO result) {
        result.add(linkTo(methodOn(LocationTypeController.class).findByPKey(result.getpKey())).withRel("location-types-findbypkey"));
        return result;
    }
}
