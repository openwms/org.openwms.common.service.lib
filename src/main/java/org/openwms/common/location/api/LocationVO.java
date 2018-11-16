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
package org.openwms.common.location.api;

import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

/**
 * A LocationVO.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class LocationVO extends ResourceSupport implements Serializable {

    private String locationId;
    private String locationGroupName;

    public String getLocationGroupName() {
        return locationGroupName;
    }

    public void setLocationGroupName(String locationGroupName) {
        this.locationGroupName = locationGroupName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
