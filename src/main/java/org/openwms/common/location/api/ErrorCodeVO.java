/*
 * Copyright 2005-2019 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A ErrorCodeVO.
 *
 * @author Heiko Scherrer
 */
public class ErrorCodeVO implements Serializable {

    @JsonProperty
    private String errorCode = "********";
    @JsonProperty
    private int plcState;

    public ErrorCodeVO() {
    }

    public ErrorCodeVO(String errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCodeVO(int plcState) {
        this.plcState = plcState;
    }

    public ErrorCodeVO(String errorCode, int plcState) {
        this.errorCode = errorCode;
        this.plcState = plcState;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getPlcState() {
        return plcState;
    }

    public void setPlcState(int plcState) {
        this.plcState = plcState;
    }
}
