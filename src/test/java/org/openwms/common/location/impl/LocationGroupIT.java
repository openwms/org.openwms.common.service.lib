/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.location.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonDataTest;
import org.openwms.common.location.LocationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A LocationGroupIT.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
class LocationGroupIT {

    private static final String KNOWN_LG = "KNOWN";
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LocationGroupRepository repository;

    /**
     * Setup data.
     */
    @BeforeEach
    void onBefore() {
        LocationGroup knownLG = new LocationGroup(KNOWN_LG);
        entityManager.persist(knownLG);
        entityManager.flush();
        entityManager.clear();
    }


    /**
     * Creating two groups with same id must fail.
     */
    @Test void testNameConstraint() {
        LocationGroup entity = new LocationGroup(KNOWN_LG);
        assertThatThrownBy(
                () -> repository.saveAndFlush(entity))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
