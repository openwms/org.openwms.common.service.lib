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
package org.openwms.common.location;

import org.junit.jupiter.api.Test;
import org.openwms.common.CommonDataTest;
import org.openwms.common.location.impl.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A LocationIT.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
class LocationIT {

    @Autowired
    private LocationRepository repository;

    /**
     * Creating two groups with same id must fail.
     */
    @Test void testNameConstraint() {
        Location loc2 = new Location(LocationPK.newBuilder().area("EXT_").aisle("0000").x("0000").y("0000").z("0000").build());
        assertThatThrownBy(
                () -> repository.saveAndFlush(loc2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
