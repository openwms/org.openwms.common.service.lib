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
package org.openwms.common.transport.impl;

import org.junit.jupiter.api.Test;
import org.openwms.common.CommonDataTest;
import org.openwms.common.location.LocationType;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.TypePlacingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A TransportUnitTypeTest.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
class TransportUnitTypeIT {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransportUnitTypeRepository repository;

    @Test void testUniqueConstraint() {
        repository.saveAndFlush(new TransportUnitType("TUT1"));
        var tut1 = new TransportUnitType("TUT1");
        assertThatThrownBy(
                () -> repository.saveAndFlush(tut1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void testCascadingTypePlacingRuleWithOrphan() {
        var locationType = new LocationType("conveyor");
        entityManager.persist(locationType);
        entityManager.flush();

        var cartonType = new TransportUnitType("Carton Type");
        var typePlacingRule = new TypePlacingRule(cartonType, locationType, 1);
        cartonType.addTypePlacingRule(typePlacingRule);
        var tpr = entityManager.getEntityManager().createQuery("select tpr from TypePlacingRule tpr", TypePlacingRule.class).getResultList();
        var before = tpr.size();
        cartonType = entityManager.merge(cartonType);

        tpr = entityManager.getEntityManager().createQuery("select tpr from TypePlacingRule tpr", TypePlacingRule.class).getResultList();
        assertThat(tpr.size() - before).isEqualTo(1);

        cartonType.removeTypePlacingRule(typePlacingRule);
        entityManager.merge(cartonType);
        tpr = entityManager.getEntityManager().createQuery("select tpr from TypePlacingRule tpr", TypePlacingRule.class).getResultList();
        assertThat(tpr).hasSize(before);
    }

    @Test void testCascadingTypePlacingRule() {
        var locationType = entityManager.find(LocationType.class,1000L);

        var cartonType = new TransportUnitType("Carton Type");
        var typePlacingRule = new TypePlacingRule(cartonType, locationType, 1);
        cartonType.addTypePlacingRule(typePlacingRule);
        cartonType = entityManager.merge(cartonType);

        var tpr = entityManager.getEntityManager().createQuery("select tpr from TypePlacingRule tpr", TypePlacingRule.class).getResultList();
        var before = tpr.size();

        entityManager.remove(cartonType);
        tpr = entityManager.getEntityManager().createQuery("select tpr from TypePlacingRule tpr", TypePlacingRule.class).getResultList();
        assertThat(before - tpr.size()).isEqualTo(1);
    }
}
