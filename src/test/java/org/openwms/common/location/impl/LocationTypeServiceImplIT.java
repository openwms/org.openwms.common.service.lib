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

import org.ameba.i18n.Translator;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonDataTest;
import org.openwms.common.location.LocationType;
import org.openwms.common.location.LocationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A LocationTypeServiceImplIT.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
@DataJpaTest(
        showSql = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = LocationTypeService.class),
        properties = {
            "spring.cloud.config.enabled=false",
            "spring.cloud.config.discovery.enabled=false",
            "spring.cloud.discovery.enabled=false",
            "spring.jpa.show-sql=false",
            "spring.main.banner-mode=OFF",
            "spring.jackson.serialization.INDENT_OUTPUT=true"
        }
)
class LocationTypeServiceImplIT {

    @Autowired
    private LocationTypeService testee;
    @MockitoBean
    private Translator translator;

    @Test void findAll() {
        List<LocationType> all = testee.findAll();
        assertThat(all).hasSizeGreaterThan(1);
    }

    @Test void delete() {
        LocationType saved = testee.save(new LocationType("TEST"));
        assertThat(saved.isNew()).isFalse();
        int size = testee.findAll().size();
        testee.delete(Collections.singletonList(new LocationType("TEST")));
        assertThat(size - testee.findAll().size()).isEqualTo(1);
    }

    @Test void save() {
        LocationType fg = new LocationType("FG");
        fg.setDescription("");
        fg = testee.save(fg);
        assertThat(fg.getDescription()).isEmpty();
    }
}