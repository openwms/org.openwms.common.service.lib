/*
 * Copyright 2018 Heiko Scherrer
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.EntityManager;

/**
 * A ObjectFactory.
 *
 * @author Heiko Scherrer
 */
@Configurable
public class ObjectFactory {

    @Autowired
    private static EntityManager em;

    private ObjectFactory() {
    }

    /**
     * Create a new TransportUnit.
     *
     * @param unitId The unique id of the Barcode
     * @return The instance
     */
    public static TransportUnit createTransportUnit(String unitId) {
        return new TransportUnit(unitId);
    }

    /**
     * Create a new TransportUnit.
     *
     * @param unitId The unique id of the Barcode
     * @param tut The TransportUnitType to set
     * @return The instance
     */
    public static TransportUnit createTransportUnit(String unitId, TransportUnitType tut) {
        TransportUnit tu = new TransportUnit(unitId);
        tu.setTransportUnitType(tut);
        return tu;
    }

    /**
     * Create a new TransportUnit.
     *
     * @param unitId The unique id of the Barcode
     * @param transportUnitTypeBk The business key of the TransportUnitType
     * @return The instance
     */
    public static TransportUnit createTransportUnit(String unitId, String transportUnitTypeBk) {
        TransportUnit tu = new TransportUnit(unitId);
        tu.setTransportUnitType(em.createQuery("select tu from TransportUnitType tu where tu.type = ?1", TransportUnitType.class).setParameter(1, transportUnitTypeBk).getSingleResult());
        return tu;
    }

    /**
     * Factory method to create a new TransportUnitType.
     *
     * @param type The business key
     * @return The instance
     */
    public static TransportUnitType createTransportUnitType(String type) {
        return new TransportUnitType(type);
    }
}
