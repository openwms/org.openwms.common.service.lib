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
package org.openwms.common.location.api.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.common.location.api.ValidationGroups;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A LocationMO is a Message Object (MO) uses as DTO between services that represents a {@code Location}.
 *
 * @author Heiko Scherrer
 */
public record LocationMO (

        /* The persistent key of the {@code Location}. */
        @NotEmpty(groups = ValidationGroups.SetLocationEmpty.class)
        @JsonProperty("pKey")
        String pKey,

        /* The accountId the {@code Location} belongs to. */
        @JsonProperty("accountId")
        String accountId,

        /* The business key of the {@code Location}. */
        @JsonProperty("id")
        String id,

        /* PLC code of the {@code Location}. */
        @JsonProperty("plcCode")
        String plcCode,

        /* ERP code of the {@code Location}. */
        @JsonProperty("erpCode")
        String erpCode,

        /* If the {@code Location} is available for inbound operations. */
        @JsonProperty("incomingActive")
        Boolean incomingActive,

        /* If the {@code Location} is available for outbound operations. */
        @JsonProperty("outgoingActive")
        Boolean outgoingActive,

        /* The PLC state - '0' means not locked for any operation. */
        @JsonProperty("plcState")
        Integer plcState

) implements Serializable {

    public static LocationMO ofPKey(String pKey) {
        return new LocationMO(pKey, null, null, null, null, null, null, null);
    }

    public static LocationMO ofId(String id) {
        return new LocationMO(null, null, id, null, null, null, null, null);
    }

    public static LocationMO ofErpCode(String erpCode) {
        return new LocationMO(null, null, null, null, erpCode, null, null, null);
    }
}
