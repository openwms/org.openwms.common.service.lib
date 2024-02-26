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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A LocationTypeVO defines a type of {@code Location}s with same characteristics.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class LocationTypeVO extends RepresentationModel<LocationTypeVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.common.location-type-v1+json";

    /** The persistent technical key of the {@code LocationType}. */
    @JsonProperty("pKey")
    private String pKey;

    /** Unique identifier. */
    @NotBlank
    @JsonProperty("type")
    private String type;

    /** A descriptive text. */
    @JsonProperty("description")
    private String description;

    /** Length. */
    @JsonProperty("length")
    private int length;

    /** Width. */
    @JsonProperty("width")
    private int width;

    /** Height. */
    @JsonProperty("height")
    private int height;

    /** Timestamp when the {@code LocationType} has been created. */
    @JsonProperty("createDt")
    private LocalDateTime createDt;

    /*~-------------------- constructors --------------------*/
    protected LocationTypeVO() { }

    @ConstructorProperties("type")
    public LocationTypeVO(String type) {
        super();
        this.type = type;
    }

    /*~-------------------- accessors --------------------*/
    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    /*~-------------------- overrides --------------------*/
    /**
     * {@inheritDoc}
     *
     * The type only.
     */
    @Override
    public String toString() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationTypeVO that = (LocationTypeVO) o;
        return length == that.length && width == that.width && height == that.height && Objects.equals(pKey, that.pKey) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(createDt, that.createDt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, type, description, length, width, height, createDt);
    }
}
