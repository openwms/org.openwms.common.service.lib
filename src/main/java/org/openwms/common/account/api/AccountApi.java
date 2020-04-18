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
package org.openwms.common.account.api;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.openwms.common.account.api.AccountApiConstants.API_ACCOUNTS;

/**
 * A AccountApi.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "accountApi", decode404 = true)
public interface AccountApi {

    /**
     * Find and return all existing {@code Account}s.
     *
     * @return An (empty) list of all Accounts.
     */
    @GetMapping(API_ACCOUNTS)
    @Cacheable("accounts")
    List<AccountVO> findAll();

    /**
     * Find and return an {@code Account}.
     *
     * @param identifier The Account's identifier
     * @return The Account instance or 404-Not Found
     */
    @GetMapping(value = API_ACCOUNTS, params = "identifier")
    @Cacheable("accounts")
    AccountVO findByIdentifier(@RequestParam("identifier") String identifier);

    /**
     * Find and return an {@code Account}.
     *
     * @param name The Account's name
     * @return The Account instance or 404-Not Found
     */
    @GetMapping(value = API_ACCOUNTS, params = "name")
    @Cacheable("accounts")
    AccountVO findByName(@RequestParam("name") String name);

}
