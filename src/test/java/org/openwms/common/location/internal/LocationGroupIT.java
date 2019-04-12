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
package org.openwms.common.location.internal;

import org.ameba.test.categories.IntegrationTests;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openwms.common.location.LocationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * A LocationGroupIT.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@RunWith(SpringRunner.class)
@Category(IntegrationTests.class)
public class LocationGroupIT {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String KNOWN_LG = "KNOWN";
    private LocationGroup knownLG;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LocationGroupRepository repository;

    /**
     * Setup data.
     */
    @Before
    public void onBefore() {
        knownLG = new LocationGroup(KNOWN_LG);
        entityManager.persist(knownLG);
        entityManager.flush();
        entityManager.clear();
    }


    /**
     * Creating two groups with same id must fail.
     */
    @Test
    public final void testNameConstraint() {
        thrown.expect(DataIntegrityViolationException.class);
        repository.save(new LocationGroup(KNOWN_LG));
    }
}
