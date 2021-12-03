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

import org.openwms.common.location.api.LocationGroupMode;

import java.io.Serializable;

/**
 * A LocationGroupMO is a Message Object (MO) uses as DTO between services that represents a {@code LocationGroup}.
 *
 * @author Heiko Scherrer
 */
public class LocationGroupMO implements Serializable {

    private static final long serialVersionUID = -1321439795390598796L;

    /** The business key of the LocationGroup. */
    private String name;
    /** The accountId the LocationGroup belongs to. */
    private String accountId;
    /** The business key of the parent LocationGroup .*/
    private String parent;
    /** The current operation mode the LocationGroup is set to. */
    private String operationMode;
    /** If the LocationGroup is available for inbound operations. */
    private boolean incomingActive;
    /** If the LocationGroup is available for outbound operations. */
    private boolean outgoingActive;

    /*~ ------------------ methods ----------------------*/
    public boolean inInfeedMode() {
        return LocationGroupMode.INFEED.equals(this.operationMode) ||
                LocationGroupMode.INFEED_AND_OUTFEED.equals(this.operationMode);
    }

    public boolean inOutfeedMode() {
        return LocationGroupMode.OUTFEED.equals(this.operationMode) ||
                LocationGroupMode.INFEED_AND_OUTFEED.equals(this.operationMode);
    }

    /*~ ------------------ accessors ----------------------*/
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
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
}
