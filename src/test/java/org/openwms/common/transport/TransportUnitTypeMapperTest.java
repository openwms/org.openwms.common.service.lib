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
package org.openwms.common.transport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.transport.api.TransportUnitTypeVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A TransportUnitTypeMapperTest.
 *
 * @author Heiko Scherrer
 */
class TransportUnitTypeMapperTest {

    private TransportUnitTypeMapper testee;

    @BeforeEach
    void beforeAll() {
        testee = new TransportUnitTypeMapperImpl();
    }

    @Test
    void convertFrom_VO_to_EO() {
        // arrange
        var vo = new TransportUnitTypeVO("typeName");
        vo.setOl(1L);
        vo.setCreateDt(LocalDateTime.now());
        vo.setLastModifiedDt(LocalDateTime.now());
        vo.setpKey("pKey");
        vo.setDescription("description");
        vo.setHeight("100");
        vo.setLength("200");
        vo.setWidth("300");

        // act
        var result = testee.convert(vo);

        // assert
        assertThat(result.getType()).isEqualTo("typeName");
        assertThat(result.getOl()).isZero(); // expect not mapped
        assertThat(result.getCreateDt()).isNull(); // expect not mapped
        assertThat(result.getLastModifiedDt()).isNull(); // expect not mapped
        assertThat(result.getPersistentKey()).isEqualTo("pKey");
        assertThat(result.getDescription()).isEqualTo("description");
        assertThat(result.getHeight()).isEqualTo(100);
        assertThat(result.getLength()).isEqualTo(200);
        assertThat(result.getWidth()).isEqualTo(300);

        assertThat(testee.convert((TransportUnitTypeVO) null)).isNull();
    }

    @Test
    void convertFrom_EO_to_VO() {
        // arrange
        var eo = new TransportUnitType("typeName");
        eo.setPersistentKey("pKey");
        eo.setCompatibility("compatibility");
        eo.setPayload(BigDecimal.ONE);
        eo.setWeightMax(BigDecimal.TWO);
        eo.setWeightTare(BigDecimal.TEN);
        eo.setDescription("description");
        eo.setHeight(100);
        eo.setLength(200);
        eo.setWidth(300);

        // act
        var result = testee.convertToVO(eo);

        // assert
        assertThat(result.getType()).isEqualTo("typeName");
        assertThat(result.getOl()).isZero(); // expect not mapped
        assertThat(result.getCreateDt()).isNull(); // expect not mapped
        assertThat(result.getLastModifiedDt()).isNull(); // expect not mapped
        assertThat(result.getpKey()).isEqualTo("pKey");
        assertThat(result.getDescription()).isEqualTo("description");
        assertThat(result.getHeight()).isEqualTo("100");
        assertThat(result.getLength()).isEqualTo("200");
        assertThat(result.getWidth()).isEqualTo("300");

        assertThat(testee.convertToVO((TransportUnitType) null)).isNull();
    }

    @Test
    void convertFrom_EO_to_MO() {
        // arrange
        var eo = new TransportUnitType("typeName");
        eo.setPersistentKey("pKey");
        eo.setCompatibility("compatibility");
        eo.setPayload(BigDecimal.ONE);
        eo.setWeightMax(BigDecimal.TWO);
        eo.setWeightTare(BigDecimal.TEN);
        eo.setDescription("description");
        eo.setHeight(100);
        eo.setLength(200);
        eo.setWidth(300);

        // act
        var result = testee.convertToMO(eo);

        // assert
        assertThat(result.getType()).isEqualTo("typeName");
        assertThat(result.getCompatibility()).isEqualTo("compatibility");
        assertThat(result.getPayload()).isEqualTo(BigDecimal.ONE);
        assertThat(result.getWeightMax()).isEqualTo(BigDecimal.TWO);
        assertThat(result.getWeightTare()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getHeight()).isEqualTo(100);
        assertThat(result.getLength()).isEqualTo(200);
        assertThat(result.getWidth()).isEqualTo(300);

        assertThat(testee.convertToMO(null)).isNull();
    }
}