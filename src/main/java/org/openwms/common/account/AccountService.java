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
package org.openwms.common.account;

import java.util.List;
import java.util.Optional;

/**
 * A AccountService.
 *
 * @author Heiko Scherrer
 */
public interface AccountService {

    /**
     * Find and return a list of all existing {@link Account}s.
     *
     * @return A list, never {@literal null}
     */
    List<Account> findAll();

    /**
     * Find and return the {@link Account}.
     *
     * @param identifier The Account's identifier
     * @return The Account instance
     */
    Optional<Account> findByIdentifier(String identifier);

    /**
     * Find and return the {@link Account}.
     *
     * @param name The Account's name
     * @return The Account instance
     */
    Optional<Account> findByName(String name);

    /**
     *
     * @return
     */
    Optional<Account> findDefault();
}
