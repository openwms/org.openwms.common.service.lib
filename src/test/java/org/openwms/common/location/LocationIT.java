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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.openwms.common.location.StringListConverter.STRING_LIST_LENGTH;

/**
 * A LocationIT.
 *
 * @author Heiko Scherrer
 */
@CommonDataTest
class LocationIT {

    @Autowired
    private LocationRepository repository;
    @Autowired
    private TestEntityManager em;

    /**
     * Creating two groups with same id must fail.
     */
    @Test void testNameConstraint() {
        Location loc2 = new Location(LocationPK.newBuilder().area("EXT_").aisle("0000").x("0000").y("0000").z("0000").build());
        assertThatThrownBy(
                () -> repository.saveAndFlush(loc2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void shall_persist_with_labels() {
        Location l = new Location(LocationPK.newBuilder().area("UNKW").aisle("0000").x("0000").y("0000").z("0000").build());
        l.setLabels(asList("L1", "L2"));
        l = repository.saveAndFlush(l);

        // Clear and load by query to get a new instance
        em.getEntityManager().clear();
        Location location = em.getEntityManager().createQuery("select l from Location l where l.pk = :pk", Location.class)
                .setParameter("pk", l.getPk()).getSingleResult();
        assertThat(l.getLabels()).isEqualTo(location.getLabels());
    }

    @Test void shall_fail_with_too_long_strings() {
        Location l = new Location(LocationPK.newBuilder().area("UNKW").aisle("0000").x("0000").y("0000").z("0000").build());
        l.setDescription(" ".repeat(256));
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> repository.saveAndFlush(l));
        assertThat(ex.getMessage()).contains("propertyPath=description");

        l.setDescription("");
        l.setClassification(" ".repeat(256));
        ex = assertThrows(ConstraintViolationException.class, () -> repository.saveAndFlush(l));
        assertThat(ex.getMessage()).contains("propertyPath=classification");

        l.setClassification("");
        l.setLabels(asList(new String[STRING_LIST_LENGTH+1]));
        ex = assertThrows(ConstraintViolationException.class, () -> repository.saveAndFlush(l));
        assertThat(ex.getMessage()).contains("propertyPath=labels");
    }
}
