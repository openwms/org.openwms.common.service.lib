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
package org.openwms.common.location;

import org.ameba.test.categories.IntegrationTests;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openwms.common.location.impl.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * A LocationIT.
 *
 * @author Heiko Scherrer
 */
@RunWith(SpringRunner.class)
@Category(IntegrationTests.class)
public class LocationIT {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private LocationRepository repository;

    /**
     * Creating two groups with same id must fail.
     */
    @Test
    public final void testNameConstraint() {
        Location loc1 = new Location(LocationPK.newBuilder().area("area").aisle("aisle").x("x").y("y").z("z").build());
        repository.save(loc1);
        Location loc2 = new Location(LocationPK.newBuilder().area("area").aisle("aisle").x("x").y("y").z("z").build());
        thrown.expect(DataIntegrityViolationException.class);
        repository.save(loc2);
    }
}
