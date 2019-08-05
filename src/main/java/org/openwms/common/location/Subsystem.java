/*
 * Copyright 2019 Heiko Scherrer
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
package org.openwms.common.location;

/**
 * A Subsystem.
 *
 * @author Heiko Scherrer
 */

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
public class Subsystem {
    /**
     * Name of the subsystem (e.g. PLC), tied to this {@code LocationGroup}.
     * A subsystem may be named after a name and a code, like PLCCONV_01, where PLCCONV is the name of
     * the subsystem group and the code _01 is a unique number in this group.
     */
    @Column(name = "C_SYSTEM_NAME")
    private String systemName;

    /**
     * Subsequential code of the subsystem (e.g. PLC), tied to this {@code LocationGroup}.
     * A subsystem may be named after a name and a code, like PLCCONV_01, where PLCCONV is the name of
     * the subsystem group and the code _01 is a unique number in this group.
     */
    @Column(name = "C_SYSTEM_CODE")
    private String systemCode;

    /*~ ----------------------------- constructors ------------------- */
    /** Dear JPA... */
    protected Subsystem() { }

    public Subsystem(@NotEmpty String systemName) {
        this.systemName = systemName;
    }

    public Subsystem(@NotEmpty String systemName, @NotEmpty String systemCode) {
        this.systemName = systemName;
        this.systemCode = systemCode;
    }

    /*~ ----------------------------- accessors ------------------- */
    /**
     * Returns the systemName.
     *
     * @return The systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Set the systemName.
     *
     * @param systemName The systemName to set
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * Returns the systemCode.
     *
     * @return The systemCode
     */
    public String getSystemCode() {
        return systemCode;
    }

    /**
     * Set the systemCode.
     *
     * @param systemCode The systemCode to set
     */
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

}