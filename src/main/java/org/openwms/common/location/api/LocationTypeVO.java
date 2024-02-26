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
import java.io.Serializable;
import java.util.Objects;

/**
 * A LocationTypeVO represents a {@code LocationType}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class LocationTypeVO extends RepresentationModel<LocationTypeVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.common.location-type-v1+json";

    @JsonProperty("pKey")
    private String pKey;
    @NotBlank
    @JsonProperty("type")
    private String type;
    @JsonProperty("description")
    private String description;
    @JsonProperty("length")
    private int length;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;

    /*~-------------------- constructors --------------------*/
    protected LocationTypeVO() {
        super();
    }

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

    /*~-------------------- overrides --------------------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationTypeVO that = (LocationTypeVO) o;
        return length == that.length && width == that.width && height == that.height && Objects.equals(pKey, that.pKey) && Objects.equals(type, that.type) && Objects.equals(description, that.description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, type, description, length, width, height);
    }
}
