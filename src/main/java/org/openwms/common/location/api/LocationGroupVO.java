/*
 * Copyright 2005-2023 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A LocationGroupVO represents a {@code LocationGroup}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class LocationGroupVO extends RepresentationModel<LocationGroupVO> implements TargetVO, Serializable {

    @JsonProperty("pKey")
    private String pKey;
    @NotBlank
    @JsonProperty("name")
    private String name;
    @JsonProperty("accountId")
    private String accountId;
    @JsonProperty("groupType")
    private String groupType;
    @JsonProperty("parentName")
    private String parent;
    @JsonProperty("operationMode")
    private String operationMode;
    @JsonProperty("groupStateIn")
    private LocationGroupState groupStateIn;
    @JsonProperty("groupStateOut")
    private LocationGroupState groupStateOut;
    @JsonProperty("childLocationGroups")
    private List<LocationGroupVO> children;

    private LocalDateTime createDt;

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    /*~ ------------------ constructors ----------------------*/
    public LocationGroupVO() {}

    public LocationGroupVO(String name) {
        this.name = name;
    }

    public LocationGroupVO(String name, LocationGroupState groupStateIn, LocationGroupState groupStateOut) {
        this.name = name;
        this.groupStateIn = groupStateIn;
        this.groupStateOut = groupStateOut;
    }

    /*~ ------------------ methods ----------------------*/

    public Stream<LocationGroupVO> streamLocationGroups() {
        return Stream.concat(
                Stream.of(this),
                children == null ? Stream.empty() : children.stream().flatMap(LocationGroupVO::streamLocationGroups));
    }

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

    public void setGroupStateIn(LocationGroupState groupStateIn) {
        this.groupStateIn = groupStateIn;
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

    public void setGroupStateOut(LocationGroupState groupStateOut) {
        this.groupStateOut = groupStateOut;
    }

    /*~ ------------------ accessors ----------------------*/
    public String getOperationMode() {
        return operationMode == null ? "" : operationMode;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<LocationGroupVO> getChildren() {
        return children;
    }

    public LocationGroupVO addChild(LocationGroupVO child) {
        if (child == null) {
            throw new IllegalArgumentException("Child to add must not be null");
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        return this;
    }

    public void setChildren(List<LocationGroupVO> children) {
        this.children = children;
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
        if (this == o) return true;
        if (!(o instanceof LocationGroupVO)) return false;
        if (!super.equals(o)) return false;
        LocationGroupVO that = (LocationGroupVO) o;
        return Objects.equals(pKey, that.pKey) && Objects.equals(name, that.name) && Objects.equals(accountId, that.accountId) && Objects.equals(groupType, that.groupType) && Objects.equals(parent, that.parent) && Objects.equals(operationMode, that.operationMode) && groupStateIn == that.groupStateIn && groupStateOut == that.groupStateOut && Objects.equals(children, that.children);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, name, accountId, groupType, parent, operationMode, groupStateIn, groupStateOut, children);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
