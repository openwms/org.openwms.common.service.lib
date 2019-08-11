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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;
import java.util.Objects;

/**
 * A LocationGroupVO is the view object of a Location.
 *
 * @author Heiko Scherrer
 */
public class LocationGroupVO extends ResourceSupport implements TargetVO, Serializable {

    @JsonProperty
    private String pKey;
    @JsonProperty
    private String name;
    @JsonProperty
    private String parent;
    @JsonProperty
    private String operationMode;
    @JsonProperty
    private LocationGroupState groupStateIn;
    @JsonProperty
    private LocationGroupState groupStateOut;

    /*~ ------------------ constructors ----------------------*/
    protected LocationGroupVO() {
    }

    public LocationGroupVO(String name, LocationGroupState groupStateIn, LocationGroupState groupStateOut) {
        this.name = name;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
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
     * Checks whether the LocationGroup is available for outfeed.
     *
     * @return {@literal true} if available, otherwise {@literal false}
     */
    @JsonIgnore
    public boolean isOutgoingActive() {
        return this.groupStateOut == LocationGroupState.AVAILABLE;
    }

    /**
     * Set the outfeed mode.
     *
     * @param outgoingActive {@literal true} if available for outfeed otherwise {@literal false}
     */
    public void setOutgoingActive(boolean outgoingActive) {
        this.groupStateIn = outgoingActive ? LocationGroupState.AVAILABLE : LocationGroupState.NOT_AVAILABLE;
    }

    /*~ ------------------ accessors ----------------------*/
    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

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

    /*~ ------------------ overrides ----------------------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        LocationGroupVO that = (LocationGroupVO) o;
        return Objects.equals(pKey, that.pKey) && Objects.equals(name, that.name) && Objects.equals(parent, that.parent) && groupStateIn == that.groupStateIn && groupStateOut == that.groupStateOut;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, name, parent, groupStateIn, groupStateOut);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return name;
    }
}