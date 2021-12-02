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
package org.openwms.common.location.api.messages;

import org.openwms.common.location.api.ValidationGroups;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A LocationMO is a Message Object (MO) uses as DTO between services that represents a {@code Location}.
 *
 * @author Heiko Scherrer
 */
public class LocationMO implements Serializable {

    private static final long serialVersionUID = 5322330486887781251L;

    /** The persistent key of the Location. */
    @NotEmpty(groups = ValidationGroups.SetLocationEmpty.class)
    private String pKey;
    /** The business key of the Location. */
    private String id;
    /** The accountId the Location belongs to. */
    private String accountId;
    /** If the Location is available for inbound operations. */
    private Boolean incomingActive;
    /** If the Location is available for outbound operations. */
    private Boolean outgoingActive;
    /** The PLC state - '0' means not locked for any operation. */
    private Integer plcState;

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Boolean getIncomingActive() {
        return incomingActive;
    }

    public void setIncomingActive(Boolean incomingActive) {
        this.incomingActive = incomingActive;
    }

    public Boolean getOutgoingActive() {
        return outgoingActive;
    }

    public void setOutgoingActive(Boolean outgoingActive) {
        this.outgoingActive = outgoingActive;
    }

    public Integer getPlcState() {
        return plcState;
    }

    public void setPlcState(Integer plcState) {
        this.plcState = plcState;
    }
}
