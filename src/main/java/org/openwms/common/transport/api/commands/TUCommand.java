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
package org.openwms.common.transport.api.commands;

import org.openwms.common.transport.api.messages.TransportUnitMO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * A TUCommand.
 *
 * @author Heiko Scherrer
 */
public class TUCommand implements Command<TUCommand.Type>, Serializable {

    @NotNull
    private Type type;
    @NotNull @Valid
    private TransportUnitMO transportUnit;

    /*~-------------------- constructors --------------------*/
    @ConstructorProperties({"type", "transportUnit"})
    protected TUCommand(Type type, TransportUnitMO transportUnit) {
        this.type = type;
        this.transportUnit = transportUnit;
    }

    private TUCommand(Builder builder) {
        this.type = builder.type;
        this.transportUnit = builder.transportUnit;
    }

    /*~-------------------- methods --------------------*/
    public static Builder newBuilder(Type type) {
        return new Builder(type);
    }

    public enum Type {
        /** Command to request and return the current instance of the TransportUnit from the golden source. */
        REQUEST,
        /** Response Command to update the foreign cache with the transmitted TransportUnit instance. */
        UPDATE_CACHE,
        /** Command to create a new TransportUnit instance. */
        CREATE,
        /** Vote whether all voters are fine with removing an instance. */
        REMOVING,
        /** Response Command to remove the TransportUnit. */
        REMOVE,
        /** Command to change the Target of a TransportUnit. */
        CHANGE_TARGET,
        /** Command to change the actual Location of a TransportUnit. */
        CHANGE_ACTUAL_LOCATION
    }

    /*~-------------------- accessors --------------------*/
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TransportUnitMO getTransportUnit() {
        return transportUnit;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TUCommand.class.getSimpleName() + "[", "]").add("type=" + type).add("transportUnit=" + transportUnit).toString();
    }


    public static final class Builder {
        private Type type;
        private TransportUnitMO transportUnit;

        private Builder(@NotNull Type type) {
            this.type = type;
        }

        public Builder withTransportUnit(@NotNull TransportUnitMO val) {
            transportUnit = val;
            return this;
        }

        public TUCommand build() {
            return new TUCommand(this);
        }
    }
}
