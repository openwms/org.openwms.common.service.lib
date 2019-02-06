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
package org.openwms.common.transport.api;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A TUCommand.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class TUCommand implements Serializable {

    @NotNull
    private Type type;
    @NotEmpty
    private String pKey;
    @NotEmpty
    private String id;
    private String actualLocation;
    private String target;

    @JsonCreator
    protected TUCommand() {
    }

    private TUCommand(Builder builder) {
        setType(builder.type);
        setpKey(builder.pKey);
        setId(builder.id);
        setActualLocation(builder.actualLocation);
        setTarget(builder.target);
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

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

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "TUCommand{" + "type=" + type + ", pKey='" + pKey + '\'' + ", id='" + id + '\'' + ", actualLocation='" + actualLocation + '\'' + ", target='" + target + '\'' + '}';
    }

    public static final class Builder {
        private Type type;
        private String pKey;
        private String id;
        private String actualLocation;
        private String target;

        private Builder() {
        }

        public Builder withType(Type val) {
            type = val;
            return this;
        }

        public Builder withPKey(String val) {
            pKey = val;
            return this;
        }

        public Builder withId(String val) {
            id = val;
            return this;
        }

        public Builder withActualLocation(String val) {
            actualLocation = val;
            return this;
        }

        public Builder withTarget(String val) {
            target = val;
            return this;
        }

        public TUCommand build() {
            return new TUCommand(this);
        }
    }
}
