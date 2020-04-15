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
package org.openwms.common.transport.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.openwms.common.CommonConstants.API_TRANSPORT_UNIT;
import static org.openwms.common.CommonConstants.API_TRANSPORT_UNITS;

/**
 * A TransportUnitApi is the public REST API to manage {@code TransportUnits}. It is implemented by a {@code Feign} client stub.
 *
 * @author Heiko Scherrer
 * @see org.springframework.cloud.openfeign.FeignClient
 */
@FeignClient(name = "common-service", qualifier = "transportUnitApi")
public interface TransportUnitApi {

    /*~----------------------------- Finders --------------------------------*/
    /**
     * Find and return a {@code TransportUnit} identified by its business key (Barcode).
     *
     * @param transportUnitBK The unique (physical) identifier
     * @return The instance or the implementation may return a 404-Not Found
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bk"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    TransportUnitVO findTransportUnit(
            @RequestParam("bk") String transportUnitBK
    );

    /**
     * Find and return all {@code TransportUnit} identified by their business keys (Barcodes).
     *
     * @param transportUnitBKs The unique (physical) identifiers
     * @return All TransportUnits or an empty List never {@literal null}
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"bks"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    List<TransportUnitVO> findTransportUnits(
            @RequestParam("bks") List<String> transportUnitBKs
    );

    /**
     * Find and return all {@code TransportUnit}s that are booked on the given Location.
     *
     * @param actualLocation The Location to search for TransportUnits
     * @return All TransportUnits on that Location or an empty List never {@literal null}
     */
    @GetMapping(value = API_TRANSPORT_UNITS, params = {"actualLocation"}, produces = "application/vnd.openwms.transport-unit-v1+json")
    List<TransportUnitVO> findTransportUnitsOn(
            @RequestParam("actualLocation") String actualLocation
    );

    /*~----------------------------- Creators --------------------------------*/
    /**
     * Create a {@code TransportUnit} with the given business key (Barcode).
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param tu Detailed information of the {@code TransportUnit} to create
     * @param strict If the {@code TransportUnit} already exists and this is {@code true}
     * an error is thrown. If {@code false}, the implementation exists silently (default)
     */
    @PostMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    void createTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestBody TransportUnitVO tu,
            @RequestParam(value = "strict", required = false) Boolean strict
    );

    /**
     * Create a {@code TransportUnit} with the given (minimal) information.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param actualLocation The current location of the {@code TransportUnit}
     * @param tut The type ({@code TransportUnitType}
     * @param strict If the {@code TransportUnit} already exists and this is {@code true}
     * an error is thrown. If {@code false}, the implementation exists silently (default)
     */
    @PostMapping(value = API_TRANSPORT_UNITS, params = {"bk", "actualLocation", "tut"})
    void createTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("actualLocation") String actualLocation,
            @RequestParam("tut") String tut,
            @RequestParam(value = "strict", required = false) Boolean strict
    );

    /*~----------------------------- Others --------------------------------*/
    /**
     * Update an existing {@code TransportUnit}.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param tu Detailed structure of the {@code TransportUnit} to create
     * @return The updated instance
     */
    @PutMapping(value = API_TRANSPORT_UNITS, params = {"bk"})
    TransportUnitVO updateTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestBody TransportUnitVO tu
    );

    /**
     * Move a {@code TransportUnit} from its current location to the {@code newLocation}.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param newLocation The new {@code Location} to move to
     * @return The updated instance
     */
    @PatchMapping(value = API_TRANSPORT_UNITS, params = {"bk", "newLocation"})
    TransportUnitVO moveTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("newLocation") String newLocation
    );

    /**
     * Add an error message to a {@code TransportUnit}.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param errorCode The message to store with the TransportUnit
     */
    @PostMapping(value = API_TRANSPORT_UNIT + "/error", params = {"bk", "errorCode"})
    void addErrorToTransportUnit(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam(value = "errorCode") String errorCode
    );
}
