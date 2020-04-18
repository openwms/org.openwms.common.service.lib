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
package org.openwms.common.account;

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * An Account encapsulates identifying information about the actual cost center. Other domain entities may be assigned to an Account and
 * therefore reserved to be used only by this Account. The Account must not be mixed with the Tenant. A Tenant can have multiple Accounts
 * and is not managed as domain object.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 */
@Entity
@Table(name = "COM_ACCOUNT")
public class Account extends ApplicationEntity implements Serializable {

    /** Unique identifier. */
    @Column(name = "C_IDENTIFIER")
    private String identifier;
    /** Name. */
    @Column(name = "C_NAME")
    private String name;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The {@code identifier}.
     *
     * @return The identifier
     */
    @Override
    public String toString() {
        return identifier;
    }

    /**
     * {@inheritDoc}
     *
     * All fields,
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(identifier, account.identifier) &&
                Objects.equals(name, account.name);
    }

    /**
     * {@inheritDoc}
     *
     * All fields,
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier, name);
    }
}
