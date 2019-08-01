/*
 * Copyright 2018 Heiko Scherrer
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

import com.fasterxml.jackson.annotation.JsonCreator;
import org.openwms.common.transport.api.messages.TransportUnitMO;

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
    @NotNull
    private TransportUnitMO transportUnit;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    protected TUCommand() {
    }

    @ConstructorProperties({"type", "transportUnit"})
    protected TUCommand(Type type, TransportUnitMO transportUnit) {
        this.type = type;
        this.transportUnit = transportUnit;
    }

    private TUCommand(Builder builder) {
        setType(builder.type);
        transportUnit = builder.transportUnit;
    }

    /*~-------------------- methods --------------------*/
    public static Builder newBuilder() {
        return new Builder();
    }

    public enum Type {
        CREATE,
        REMOVING,
        REMOVE,
        CHANGE_TARGET,
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
        private @NotNull Type type;
        private @NotNull TransportUnitMO transportUnit;

        private Builder() {
        }

        public Builder withType(@NotNull Type val) {
            type = val;
            return this;
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
