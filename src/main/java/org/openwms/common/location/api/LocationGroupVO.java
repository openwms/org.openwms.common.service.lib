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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static org.openwms.common.location.api.LocationApiConstants.DATETIME_FORMAT_ZULU;

/**
 * A LocationGroupVO represents a {@code LocationGroup}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class LocationGroupVO extends RepresentationModel<LocationGroupVO> implements TargetVO, Serializable {

    /** The persistent technical key of the {@code LocationGroup}. */
    @JsonProperty("pKey")
    private String pKey;

    /** Unique identifier of the {@code LocationGroup}. */
    @NotBlank
    @JsonProperty("name")
    private String name;

    /** The {@code LocationGroup} might be assigned to an {@code Account}. */
    @JsonProperty("accountId")
    private String accountId;

    /** Description of the {@code LocationGroup}. */
    @JsonProperty("description")
    private String description;

    /** A type can be assigned to a {@code LocationGroup}. */
    @JsonProperty("groupType")
    private String groupType;

    /** Parent {@code LocationGroup}. */
    @JsonProperty("parentName")
    private String parent;

    /** The operation mode is controlled by the subsystem and defines the physical mode a {@code LocationGroup} is currently able to operate in. */
    @NotBlank(groups = ValidationGroups.Create.class)
    @JsonProperty("operationMode")
    private String operationMode;

    /** Infeed state, controlled by the subsystem only. */
    @JsonProperty("groupStateIn")
    private LocationGroupState groupStateIn;

    /** Outfeed state. */
    @JsonProperty("groupStateOut")
    private LocationGroupState groupStateOut;

    /** Child {@code LocationGroup}s. */
    @JsonProperty("childLocationGroups")
    private List<LocationGroupVO> children;

    /** Timestamp when the {@code LocationGroup} has been created. */
    @JsonProperty("createDt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT_ZULU) // required
    private LocalDateTime createDt;

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

    /**
     * Creates a new LocationGroupVO object with the given name and operation mode.
     *
     * @param name The name of the location group.
     * @param operationMode The operation mode of the location group.
     * @return The created LocationGroupVO object.
     */
    public static LocationGroupVO create(String name, String operationMode) {
        var result = new LocationGroupVO(name);
        result.setOperationMode(operationMode);
        return result;
    }

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

    public LocationGroupState getGroupStateIn() {
        return groupStateIn;
    }

    public LocationGroupState getGroupStateOut() {
        return groupStateOut;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
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
        if (!(o instanceof LocationGroupVO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(pKey, that.pKey) && Objects.equals(name, that.name) && Objects.equals(accountId, that.accountId) && Objects.equals(description, that.description) && Objects.equals(groupType, that.groupType) && Objects.equals(parent, that.parent) && Objects.equals(operationMode, that.operationMode) && groupStateIn == that.groupStateIn && groupStateOut == that.groupStateOut && Objects.equals(children, that.children) && Objects.equals(createDt, that.createDt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, name, accountId, description, groupType, parent, operationMode, groupStateIn, groupStateOut, children, createDt);
    }

    /**
     * {@inheritDoc}
     *
     * Only the name.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a string representation of the LocationGroupVO object, including all its fields.
     * Fields are concatenated using a delimiter ", ".
     *
     * @return A string representation of the LocationGroupVO object.
     */
    public String allFieldsToString() {
        return new StringJoiner(", ", LocationGroupVO.class.getSimpleName() + "[", "]")
                .add("pKey='" + pKey + "'")
                .add("name='" + name + "'")
                .add("accountId='" + accountId + "'")
                .add("description='" + description + "'")
                .add("groupType='" + groupType + "'")
                .add("parent='" + parent + "'")
                .add("operationMode='" + operationMode + "'")
                .add("groupStateIn=" + groupStateIn)
                .add("groupStateOut=" + groupStateOut)
                .add("children=" + children)
                .add("createDt=" + createDt)
                .toString();
    }
}
