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
package org.openwms.common.transport;

import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.Index;
import org.openwms.common.transport.api.TransportUnitTypeVO;
import org.openwms.core.http.AbstractWebController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.lang.String.format;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT_TYPES;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A TransportUnitTypeController.
 *
 * @author Heiko Scherrer
 */
@Profile("!INMEM")
@RestController
public class TransportUnitTypeController extends AbstractWebController {

    private final TransportUnitTypeService service;
    private final BeanMapper mapper;

    TransportUnitTypeController(TransportUnitTypeService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = API_TRANSPORT_UNIT_TYPES, params = {"type"})
    @ResponseBody
    public TransportUnitTypeVO findTransportUnitType(@RequestParam("type") String type) {
        TransportUnitType optType = service.findByType(type)
                .orElseThrow(() -> new NotFoundException(format("No TransportUniType with type [%s] found", type)));
        return mapper.map(optType, TransportUnitTypeVO.class);
    }

    @GetMapping(API_TRANSPORT_UNIT_TYPES)
    @ResponseBody
    public List<TransportUnitTypeVO> findTransportUnitTypes() {
        List<TransportUnitType> all = service.findAll();
        return mapper.map(all, TransportUnitTypeVO.class);
    }

    @GetMapping(API_TRANSPORT_UNIT_TYPES + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitType("PALLET")).withRel("transport-unit-types-findtransportunittype"),
                        linkTo(methodOn(TransportUnitTypeController.class).findTransportUnitTypes()).withRel("transport-unit-types-findall")
                )
        );
    }
}
