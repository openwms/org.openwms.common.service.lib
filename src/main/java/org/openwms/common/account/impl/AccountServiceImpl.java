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
package org.openwms.common.account.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.account.Account;
import org.openwms.common.account.AccountService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A AccountServiceImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public List<Account> findAll() {
        return repository.findAll();
    }
}
