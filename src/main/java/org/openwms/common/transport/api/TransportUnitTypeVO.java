/*
 * Copyright 2005-2021 the original author or authors.
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
package org.openwms.common.transport.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TransportUnitTypeVO.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitTypeVO implements Serializable {

    @NotEmpty
    @JsonProperty("type")
    private String type;
    @JsonProperty("description")
    private String description;
    @NotEmpty
    @JsonProperty("height")
    private String height;
    @NotEmpty
    @JsonProperty("width")
    private String width;
    @NotEmpty
    @JsonProperty("length")
    private String length;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    protected TransportUnitTypeVO() {
    }

    public TransportUnitTypeVO(String type) {
        this.type = type;
    }

    /*~-------------------- accessors --------------------*/

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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportUnitTypeVO that = (TransportUnitTypeVO) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(description, that.description) &&
                Objects.equals(height, that.height) &&
                Objects.equals(width, that.width) &&
                Objects.equals(length, that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description, height, width, length);
    }
}
