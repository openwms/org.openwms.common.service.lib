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

import org.ameba.exception.NotFoundException;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.LocationService;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

/**
 * A TransportConfig.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
class TransportConfig {

    @Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
    @Bean
    public TopicExchange emailExchange(@Value("${owms.events.common.tu.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    /**
     * This bean is responsible to generate some test data, only in default Spring environment.
     *
     * @param tur
     * @param tutr
     * @param ls
     * @return
     */
    @Profile("DEMO")
    @Bean
    CommandLineRunner transportRunner(TransportUnitRepository tur, TransportUnitTypeRepository tutr, LocationService ls) {
        TransportUnitType tut = tutr.save(new TransportUnitType(("Carton")));
        return args -> {
            tur.deleteAll();
            Arrays.asList("4711,4712,4713".split(","))
                    .forEach(bc -> {
                        TransportUnit tu = new TransportUnit(new Barcode(bc));
                        tu.setTransportUnitType(tut);
                        tu.setActualLocation(ls.findByLocationId(LocationPK.fromString("EXT_/0000/0000/0000/0000")).orElseThrow(NotFoundException::new));
                        tur.save(tu);
                    });
        };
    }
}
