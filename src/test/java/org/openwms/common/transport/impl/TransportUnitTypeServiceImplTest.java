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

import org.ameba.exception.NotFoundException;
import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.location.LocationType;
import org.openwms.common.transport.Rule;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.TypePlacingRule;
import org.openwms.common.transport.TypeStackingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @Transactional
    void deleteType() {
        int i = service.findAll().size();
        // Delete Rules first...
        em.createQuery("delete from TypeStackingRule").executeUpdate();
        service.deleteType(new TransportUnitType("BIN"));
        assertThat(i - service.findAll().size()).isEqualTo(1);
    }

    @Test
    void save() {
        TransportUnitType transportUnitType = service.findAll().get(0);
        transportUnitType.setDescription("Jam");
        transportUnitType = service.save(transportUnitType);
        assertThat(em.find(TransportUnitType.class, transportUnitType.getPk()).getDescription()).isEqualTo("Jam");
    }

    @Test
    void addRules() {
        // Testing with transient entities...
        List<LocationType> news = new ArrayList<>(1);
        news.add(new LocationType("PG"));
        assertThatThrownBy(() -> service.updateRules("BIN", news, Collections.emptyList()))
                .isInstanceOf(ServiceLayerException.class)
                .hasMessageContaining("must be persisted before");
        news.clear();

        // Test with detached entities...
        LocationType lt1001 = em.find(LocationType.class, 1001L);
        news.add(lt1001);
        TransportUnitType bin = service.updateRules("BIN", news, Collections.emptyList());
        assertThat(bin.getTypePlacingRules().stream().anyMatch(r -> r.getAllowedLocationType().getPk().equals(1001L))).isTrue();
    }

    @Test
    void removeRules() {
        int srCount = em.createQuery("select sr from TypeStackingRule sr").getResultList().size();
        int prCount = em.createQuery("select pr from TypePlacingRule pr").getResultList().size();
        assertThat(srCount).isEqualTo(1);
        assertThat(prCount).isEqualTo(3);

        // Should not modify rules...
        service.updateRules("PALLET", null, null);
        service.updateRules("PALLET", Collections.emptyList(), Collections.emptyList());
        srCount = em.createQuery("select sr from TypeStackingRule sr").getResultList().size();
        prCount = em.createQuery("select pr from TypePlacingRule pr").getResultList().size();
        assertThat(srCount).isEqualTo(1);
        assertThat(prCount).isEqualTo(3);

        // Test with detached entities...
        List<LocationType> removes = new ArrayList<>(1);
        LocationType lt1000 = em.find(LocationType.class, 1000L);
        removes.add(lt1000);
        TransportUnitType pallet = service.updateRules("PALLET", Collections.emptyList(), removes);
        srCount = em.createQuery("select sr from TypeStackingRule sr").getResultList().size();
        prCount = em.createQuery("select pr from TypePlacingRule pr").getResultList().size();
        assertThat(srCount).isEqualTo(1);
        assertThat(prCount).isEqualTo(2);
        assertThat(pallet.getTypePlacingRules().stream().noneMatch(r -> r.getAllowedLocationType().getPk().equals(1000L))).isTrue();
    }

    @Test
    void loadRules() {
        List<Rule> rules = service.loadRules("PALLET");
        assertThatThrownBy(() -> service.loadRules(null)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.loadRules("UNKNOWN")).isInstanceOf(NotFoundException.class);
        assertThat(rules.stream().filter(r -> r instanceof TypePlacingRule).count()).isEqualTo(2);
        assertThat(rules.stream().filter(r -> r instanceof TypeStackingRule).count()).isEqualTo(1);
    }
}