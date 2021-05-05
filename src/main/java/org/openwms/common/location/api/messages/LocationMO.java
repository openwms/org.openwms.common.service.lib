/*
 * Copyright 2005-2020 the original author or authors.
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

import java.io.Serializable;

/**
 * A LocationMO is a Message Object (MO) uses as DTO between services that represents a {@code Location}.
 *
 * @author Heiko Scherrer
 */
public class LocationMO implements Serializable {

    private static final long serialVersionUID = 5322330486887781251L;

    /** The persistent key of the Location. */
    private String pKey;
    /** The business key of the Location. */
    private String id;
    /** The accountId the Location belongs to. */
    private String accountId;
    /** The business key of the parent Location .*/
    private String parent;
    /** If the Location is available for inbound operations. */
    private boolean incomingActive;
    /** If the Location is available for outbound operations. */
    private boolean outgoingActive;
    /** The PLC state - '0' means not locked for any operation. */
    private int plcState;

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isIncomingActive() {
        return incomingActive;
    }

    public void setIncomingActive(boolean incomingActive) {
        this.incomingActive = incomingActive;
    }

    public boolean isOutgoingActive() {
        return outgoingActive;
    }

    public void setOutgoingActive(boolean outgoingActive) {
        this.outgoingActive = outgoingActive;
    }

    public int getPlcState() {
        return plcState;
    }

    public void setPlcState(int plcState) {
        this.plcState = plcState;
    }
}
