/*
 * Copyright 2005-2019 the original author or authors.
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

import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.transport.TransportUnitType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A TransportUnitTypeServiceImplTest.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TransportUnitTypeServiceImplTest {

    @Autowired
    private TransportUnitTypeServiceImpl service;
    @Autowired
    private EntityManager em;

    @Test
    void findByType() {
        assertThat(service.findByType("PALLET").isPresent()).isTrue();
        assertThat(service.findByType("UNKNOWN").isPresent()).isFalse();
        assertThat(service.findByType(null).isPresent()).isFalse();
        assertThat(service.findByType("").isPresent()).isFalse();
    }

    @Test
    void findAll() {
        assertThat(service.findAll().size()).isPositive();
    }

    @Test
    void create() {
        TransportUnitType europallet = service.create(new TransportUnitType("Europallet"));
        assertThat(europallet.isNew()).isFalse();
        assertThat(em.find(TransportUnitType.class, europallet.getPk())).isEqualTo(europallet);
    }

    @Test
    void deleteType() {
        int i = service.findAll().size();
        service.deleteType(new TransportUnitType("BIN"));
        assertThat(i - service.findAll().size()).isEqualTo(1);
    }

    @Test
    void save() {
    }

    @Test
    void updateRules() {
    }

    @Test
    void loadRules() {
    }
}