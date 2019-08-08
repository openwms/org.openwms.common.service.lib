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
package org.openwms.common.location;

import java.util.List;

/**
 * A LocationTypeService offers useful methods according to the handling of {@link LocationType}s.
 *
 * @author Heiko Scherrer
 */
public interface LocationTypeService {

    /**
     * Return a list of all LocationTypes in natural order.
     *
     * @return All LocationTypes as a list
     */
    List<LocationType> findAll();

    /**
     * Delete already persisted LocationType instances.
     *
     * @param locationTypes A list of all instances to be deleted.
     */
    void delete(List<LocationType> locationTypes);

    /**
     * Save a LocationType.
     *
     * @param locationType The type to save
     * @return The saved type
     */
    LocationType save(LocationType locationType);
}
