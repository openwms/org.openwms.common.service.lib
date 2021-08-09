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
package org.openwms.common.location.api.events;

import org.openwms.common.location.api.LockMode;
import org.openwms.common.location.api.LockType;
import org.openwms.core.event.RootApplicationEvent;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TargetEvent signals changes on {@code Target}s, like a {@code Location} or a {@code LocationGroup}.
 *
 * @author Heiko Scherrer
 */
public class TargetEvent extends RootApplicationEvent implements Serializable {

    private String targetBK;
    private LockType lockType;
    private LockMode operationMode;
    private Boolean reAllocation;

    /*~ ------------------ constructors ----------------------*/
    /**
     * Create a new TargetEvent.
     *
     * @param source The business key of the Target, can be a {@code LocationPK} in String format or a LocationGroup name
     */
    protected TargetEvent(String source) {
        super(source);
        this.targetBK = source;
    }

    private TargetEvent(Builder builder) {
        super(builder.targetBK);
        targetBK = builder.targetBK;
        lockType = builder.lockType;
        operationMode = builder.operationMode;
        reAllocation = builder.reAllocation;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /*~ ------------------ accessors ----------------------*/
    public String getTargetBK() {
        return targetBK;
    }

    public LockType getLockType() {
        return lockType;
    }

    public LockMode getOperationMode() {
        return operationMode;
    }

    public Boolean getReAllocation() {
        return reAllocation;
    }

    /*~ ------------------ generated ----------------------*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetEvent)) return false;
        TargetEvent that = (TargetEvent) o;
        return Objects.equals(targetBK, that.targetBK) &&
                lockType == that.lockType &&
                operationMode == that.operationMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetBK, lockType, operationMode);
    }


    public static final class Builder {
        private String targetBK;
        private LockType lockType;
        private LockMode operationMode;
        private Boolean reAllocation;

        public Builder targetBK(String val) {
            targetBK = val;
            return this;
        }

        public Builder lockType(LockType val) {
            lockType = val;
            return this;
        }

        public Builder operationMode(LockMode val) {
            operationMode = val;
            return this;
        }

        public Builder reAllocation(Boolean val) {
            reAllocation = val;
            return this;
        }

        public TargetEvent build() {
            if (this.targetBK == null) {
                throw new IllegalArgumentException("The TargetEvent must carry the targetBK of the Target where the event occurred on");
            }
            return new TargetEvent(this);
        }
    }
}
