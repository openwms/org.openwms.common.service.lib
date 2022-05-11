/*
 * Copyright 2005-2022 the original author or authors.
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
import org.ameba.http.AbstractBase;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A TransportUnitTypeVO.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitTypeVO extends AbstractBase<TransportUnitTypeVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.transport-unit-type-v1+json";

    /** The persistent key. */
    @NotBlank
    @JsonProperty("pKey")
    private String pKey;

    /** Unique natural key. */
    @NotBlank
    @JsonProperty("type")
    private String type;

    /** Description for the TransportUnitType. */
    @JsonProperty("description")
    private String description;

    /** Height of the TransportUnitType. */
    @NotBlank
    @JsonProperty("height")
    private String height;

    /** Width of the TransportUnitType. */
    @NotBlank
    @JsonProperty("width")
    private String width;

    /** Length of the TransportUnitType. */
    @NotBlank
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

    /**
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitTypeVO)) return false;
        if (!super.equals(o)) return false;
        TransportUnitTypeVO that = (TransportUnitTypeVO) o;
        return Objects.equals(pKey, that.pKey) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(height, that.height) && Objects.equals(width, that.width) && Objects.equals(length, that.length);
    }

    /**
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, type, description, height, width, length);
    }

    /**
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", TransportUnitTypeVO.class.getSimpleName() + "[", "]")
                .add("pKey='" + pKey + "'")
                .add("type='" + type + "'")
                .add("description='" + description + "'")
                .add("height='" + height + "'")
                .add("width='" + width + "'")
                .add("length='" + length + "'")
                .toString();
    }

    public static final class Builder {
        private String type;
        private String description;
        private String height;
        private String width;
        private String length;

        private Builder() {
        }

        public static Builder aTransportUnitTypeVO() {
            return new Builder();
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withHeight(String height) {
            this.height = height;
            return this;
        }

        public Builder withWidth(String width) {
            this.width = width;
            return this;
        }

        public Builder withLength(String length) {
            this.length = length;
            return this;
        }

        public TransportUnitTypeVO build() {
            TransportUnitTypeVO transportUnitTypeVO = new TransportUnitTypeVO();
            transportUnitTypeVO.setType(type);
            transportUnitTypeVO.setDescription(description);
            transportUnitTypeVO.setHeight(height);
            transportUnitTypeVO.setWidth(width);
            transportUnitTypeVO.setLength(length);
            return transportUnitTypeVO;
        }
    }
}
