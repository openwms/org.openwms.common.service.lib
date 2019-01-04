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
package org.openwms.common.transport.inmem;

import org.ameba.annotation.TxService;
import org.openwms.common.transport.api.TransportUnitApi;
import org.openwms.common.transport.api.TransportUnitTypeVO;
import org.openwms.common.transport.api.TransportUnitVO;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * A TransportUnitApiImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("INMEM")
@TxService
class TransportUnitApiImpl implements TransportUnitApi {
    @Override
    public TransportUnitVO findTransportUnit(String barcode) {
        return null;
    }

    @Override
    public TransportUnitTypeVO findTransportUnitType(String type) {
        return null;
    }

    @Override
    public List<TransportUnitTypeVO> findTransportUnitTypes() {
        return null;
    }

    @Override
    public List<TransportUnitVO> getTransportUnitsOn(String actualLocation) {
        return null;
    }

    @Override
    public void createTU(String barcode, TransportUnitVO tu, Boolean strict) {

    }

    @Override
    public void createTU(String barcode, String actualLocation, String tut, Boolean strict) {

    }

    @Override
    public TransportUnitVO updateTU(String barcode, TransportUnitVO tu) {
        return null;
    }

    @Override
    public TransportUnitVO moveTU(String barcode, String newLocation) {
        return null;
    }
}
