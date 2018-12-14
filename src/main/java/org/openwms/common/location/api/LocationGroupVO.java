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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

/**
 * A LocationGroupVO is the view object of a Location.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class LocationGroupVO extends ResourceSupport implements Target, Serializable {

    @JsonProperty
    private String name;
    @JsonProperty
    private String parent;
    @JsonProperty
    private LocationGroupState groupStateIn;
    @JsonProperty
    private LocationGroupState groupStateOut;

    protected LocationGroupVO() {
    }

    public LocationGroupVO(String name) {
        this.name = name;
    }

    /*~ ------------------ methods ----------------------*/
    /**
     * Check whether the LocationGroup has a parent.
     *
     * @return {@literal true} if it has a parent, otherwise {@literal false}
     */
    public boolean hasParent() {
        return parent != null && !parent.isEmpty();
    }

    /**
     * Checks whether the LocationGroup is blocked for infeed.
     *
     * @return {@literal true} if blocked, otherwise {@literal false}
     */
    @JsonIgnore
    public boolean isInfeedBlocked() {
        return !isIncomingActive();
    }

    /**
     * Checks whether the LocationGroup is available for infeed.
     *
     * @return {@literal true} if available, otherwise {@literal false}
     */
    @JsonIgnore
    public boolean isIncomingActive() {
        return this.groupStateIn == LocationGroupState.AVAILABLE;
    }

    /**
     * Set the infeed mode.
     *
     * @param incomingActive {@literal true} if available for infeed otherwise {@literal false}
     */
    public void setIncomingActive(boolean incomingActive) {
        this.groupStateIn = incomingActive ? LocationGroupState.AVAILABLE : LocationGroupState.NOT_AVAILABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return name;
    }

    /*~ ------------------ accessors ----------------------*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}