/*
 * Copyright 2005-2024 the original author or authors.
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

import org.openwms.common.location.LocationGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * A LocationGroupRepository adds particular functionality regarding {@link LocationGroup} entity classes.
 * 
 * @author Heiko Scherrer
 */
interface LocationGroupRepository extends JpaRepository<LocationGroup, Long> {

    @Override
    List<LocationGroup> findAll();

    Optional<LocationGroup> findByName(String name);

    Optional<LocationGroup> findBypKey(String persistentKey);

    List<LocationGroup> findByNameIn(List<String> names);
}