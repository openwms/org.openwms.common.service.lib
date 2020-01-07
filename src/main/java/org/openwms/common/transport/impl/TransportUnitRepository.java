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
package org.openwms.common.transport.impl;

import org.openwms.common.location.Location;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A TransportUnitRepository adds particular functionality regarding {@link TransportUnit} entity classes.
 * 
 * @author Heiko Scherrer
 */
@Repository
interface TransportUnitRepository extends RevisionRepository<TransportUnit, Long, Integer>, JpaRepository<TransportUnit, Long> {

    @Query("select tu from TransportUnit tu where tu.pKey = :pKey")
    Optional<TransportUnit> findByPKey(@Param("pKey") String pKey);

    Optional<TransportUnit> findByBarcode(Barcode barcode);

    @Query("select tu from TransportUnit tu where tu.barcode in :barcodes")
    List<TransportUnit> findByBarcodeIn(@Param("barcodes") List<Barcode> barcodes);

    List<TransportUnit> findByActualLocationOrderByActualLocationDate(Location actualLocation);
}
