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
package org.openwms.common.location.impl;

import org.junit.jupiter.api.Test;
import org.openwms.common.CommonIT;
import org.openwms.common.location.LocationType;
import org.openwms.common.location.LocationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A LocationTypeServiceImplIT.
 *
 * @author Heiko Scherrer
 */
@CommonIT
@DataJpaTest(
        showSql = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = LocationTypeService.class)
)
class LocationTypeServiceImplIT {

    @Autowired
    private LocationTypeService testee;

    @Test void findAll() {
        List<LocationType> all = testee.findAll();
        assertThat(all.size()).isGreaterThan(1);
    }

    @Test void delete() {
        int size = testee.findAll().size();
        testee.delete(Collections.singletonList(new LocationType("FG")));
        assertThat(size - testee.findAll().size()).isEqualTo(1);
    }

    @Test void save() {
        LocationType fg = new LocationType("FG");
        fg.setDescription("");
        fg = testee.save(fg);
        assertThat(fg.getDescription()).isEqualTo("");
    }
}