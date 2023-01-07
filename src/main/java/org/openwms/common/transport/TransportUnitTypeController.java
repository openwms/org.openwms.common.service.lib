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
package org.openwms.common.transport;

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.openwms.common.SimpleLink;
import org.openwms.common.transport.api.TransportUnitTypeVO;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static java.lang.String.format;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT_TYPES;
import static org.openwms.common.transport.api.TransportUnitTypeVO.MEDIA_TYPE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A TransportUnitTypeController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@MeasuredRestController
public class TransportUnitTypeController extends AbstractWebController {

    private final TransportUnitTypeService service;
    private final TransportUnitTypeMapper mapper;

    TransportUnitTypeController(TransportUnitTypeService service, TransportUnitTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = API_TRANSPORT_UNIT_TYPES + "/{pKey}", produces = MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<TransportUnitTypeVO> findByPKey(@PathVariable("pKey") String pKey) {
        return ResponseEntity.ok(convertAndLinks(service.findByPKey(pKey)));
    }

    @GetMapping(value = API_TRANSPORT_UNIT_TYPES, params = {"type"}, produces = MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<TransportUnitTypeVO> findTransportUnitType(@RequestParam("type") String type) {
        var optType = service.findByType(type)
                .orElseThrow(() -> new NotFoundException(format("No TransportUniType with type [%s] found", type)));
        return ResponseEntity.ok(convertAndLinks(optType));
    }

    @GetMapping(value = API_TRANSPORT_UNIT_TYPES, produces = MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<List<TransportUnitTypeVO>> findTransportUnitTypes() {
        return ResponseEntity.ok(convertAndLinks(service.findAll()));
    }

    @GetMapping(API_TRANSPORT_UNIT_TYPES + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
            new Index(
                linkTo(methodOn(TransportUnitTypeController.class).findByPKey("{pKey}")).withRel("transport-unit-types-findbypkey"),
                linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitType("PALLET")).withRel("transport-unit-types-findtransportunittype"),
                linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitTypes()).withRel("transport-unit-types-findall")
            )
        );
    }

    private TransportUnitTypeVO addLinks(TransportUnitTypeVO result) {
        result.add(
                new SimpleLink(linkTo(methodOn(TransportUnitTypeController.class).findByPKey(result.getpKey())).withSelfRel())
        );
        return result;
    }

    private TransportUnitTypeVO convertAndLinks(TransportUnitType entity) {
        return addLinks(
                mapper.convertToVO(entity)
        );
    }

    private List<TransportUnitTypeVO> convertAndLinks(List<TransportUnitType> entities) {
        return entities.stream()
                .map(mapper::convertToVO)
                .map(this::addLinks)
                .toList();
    }
}
