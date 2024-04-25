/*
 * Copyright 2005-2024 the original author or authors.
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
package org.openwms.common.account.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.openwms.common.account.Account;
import org.openwms.common.account.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static org.openwms.common.CommonMessageCodes.ACCOUNT_NOT_FOUND_BY_PKEY;

/**
 * A AccountServiceImpl.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final Translator translator;

    AccountServiceImpl(AccountRepository repository, Translator translator) {
        this.repository = repository;
        this.translator = translator;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public @NotNull List<Account> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public @NotNull Account findByPKey(@NotBlank String pKey) {
        return repository.findBypKey(pKey)
                .orElseThrow(() -> new NotFoundException(translator, ACCOUNT_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey));
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public Optional<Account> findByIdentifier(@NotBlank String identifier) {
        return repository.findByIdentifier(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public Optional<Account> findByName(@NotBlank String name) {
        return repository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public Optional<Account> findDefault() {
        return repository.findByDefaultAccount(true);
    }
}
