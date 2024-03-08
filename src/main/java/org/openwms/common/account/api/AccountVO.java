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
package org.openwms.common.account.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import static org.openwms.common.account.api.AccountApiConstants.DATETIME_FORMAT_ZULU;

/**
 * A AccountVO is the representation object that encapsulates identifying information about the actual cost center.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountVO extends RepresentationModel<AccountVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.common.account-v1+json";

    /** The persistent technical key of the {@code Account}. */
    @JsonProperty("pKey")
    private String pKey;

    /** Unique identifier. */
    @NotBlank
    @JsonProperty("identifier")
    private String identifier;

    /** Name. */
    @NotBlank
    @JsonProperty("name")
    private String name;

    /** Timestamp when the {@code Account} has been created. */
    @JsonProperty("createDt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT_ZULU) // required
    private LocalDateTime createDt;

    /*~-------------------- accessors --------------------*/
    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*~-------------------- overrides --------------------*/
    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountVO)) return false;
        if (!super.equals(o)) return false;
        var accountVO = (AccountVO) o;
        return Objects.equals(pKey, accountVO.pKey) && Objects.equals(identifier, accountVO.identifier) && Objects.equals(name, accountVO.name) && Objects.equals(createDt, accountVO.createDt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, identifier, name, createDt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", AccountVO.class.getSimpleName() + "[", "]")
                .add("pKey='" + pKey + "'")
                .add("identifier='" + identifier + "'")
                .add("name='" + name + "'")
                .add("createDt='" + createDt + "'")
                .toString();
    }
}
