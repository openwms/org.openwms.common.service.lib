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
package org.openwms.common.location.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ErrorCodeVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorCodeVO implements Serializable {

    public static final String ALL_NOT_SET = "********";
    public static final int STATE_IN_POSITION = ALL_NOT_SET.length()-1;
    public static final int STATE_OUT_POSITION = ALL_NOT_SET.length()-2;

    public static final ErrorCodeVO LOCK_STATE_IN = new ErrorCodeVO("*******1");
    public static final ErrorCodeVO UNLOCK_STATE_IN = new ErrorCodeVO("*******0");
    public static final ErrorCodeVO LOCK_STATE_OUT = new ErrorCodeVO("******1*");
    public static final ErrorCodeVO UNLOCK_STATE_OUT = new ErrorCodeVO("******0*");
    public static final ErrorCodeVO LOCK_STATE_IN_AND_OUT = new ErrorCodeVO("******11");
    public static final ErrorCodeVO UNLOCK_STATE_IN_AND_OUT = new ErrorCodeVO("******00");

    @JsonProperty("errorCode")
    private String errorCode = ErrorCodeVO.ALL_NOT_SET;
    @JsonProperty("plcState")
    private Integer plcState;

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

    public Integer getPlcState() {
        return plcState;
    }

    public void setPlcState(Integer plcState) {
        this.plcState = plcState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorCodeVO that = (ErrorCodeVO) o;
        return Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(plcState, that.plcState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, plcState);
    }
}
