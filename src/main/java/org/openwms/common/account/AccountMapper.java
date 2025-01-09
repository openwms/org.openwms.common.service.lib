/*
 * Copyright 2005-2025 the original author or authors.
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

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openwms.common.account.api.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.openwms.common.CommonMessageCodes.ACCOUNT_NOT_FOUND_BY_ID;

/**
 * A AccountMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(uses = {Translator.class, AccountService.class}, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class AccountMapper {

    private Translator translator;
    private AccountService accountService;

    @Autowired
    public void setTranslator(Translator translator) {
        this.translator = translator;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Account convertFromId(String id) {
        if (id == null) {
            return null;
        }
        var accountOpt = accountService.findByIdentifier(id);
        if (accountOpt.isEmpty()) {
            throw new NotFoundException(translator, ACCOUNT_NOT_FOUND_BY_ID, new String[]{id}, id);
        }
        return accountOpt.get();
    }

    @Mapping(target = "pKey", source = "eo.persistentKey")
    public abstract AccountVO convertToVO(Account eo);

    public abstract List<AccountVO> convertToVO(List<Account> eo);
}
