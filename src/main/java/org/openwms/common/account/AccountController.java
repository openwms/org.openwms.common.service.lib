/*
 * Copyright 2005-2023 the original author or authors.
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
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.openwms.common.account.api.AccountVO;
import org.openwms.common.account.impl.AccountMapper;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

import static org.openwms.common.CommonMessageCodes.ACCOUNT_NOT_FOUND;
import static org.openwms.common.CommonMessageCodes.ACCOUNT_NO_DEFAULT;
import static org.openwms.common.account.api.AccountApiConstants.API_ACCOUNTS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A AccountController is a measured and Spring managed REST controller that serves Account resources.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class AccountController extends AbstractWebController {

    private final AccountService service;
    private final AccountMapper mapper;
    private final Translator translator;

    public AccountController(AccountService service, AccountMapper mapper, Translator translator) {
        this.service = service;
        this.mapper = mapper;
        this.translator = translator;
    }

    @GetMapping(value = API_ACCOUNTS, produces = AccountVO.MEDIA_TYPE)
    public ResponseEntity<List<AccountVO>> findAll() {
        return ResponseEntity.ok(
            convertAndLinks(service.findAll()
        ));
    }

    @GetMapping(value = API_ACCOUNTS + "/{pKey}", produces = AccountVO.MEDIA_TYPE)
    public ResponseEntity<AccountVO> findByPKey(@PathVariable("pKey") String pKey) {
        return ResponseEntity.ok(
            convertAndLinks(
                service.findByPKey(pKey)
        ));
    }

    @GetMapping(value = API_ACCOUNTS, params = "default", produces = AccountVO.MEDIA_TYPE)
    public ResponseEntity<AccountVO> findDefault() {
        return ResponseEntity.ok(
            convertAndLinks(
                service.findDefault().orElseThrow(
                    ()-> new NotFoundException(translator, ACCOUNT_NO_DEFAULT))
        ));
    }

    @GetMapping(value = API_ACCOUNTS, params = "identifier", produces = AccountVO.MEDIA_TYPE)
    public ResponseEntity<AccountVO> findByIdentifier(@RequestParam("identifier") String identifier) {
        return ResponseEntity.ok(
            convertAndLinks(
                service.findByIdentifier(identifier).orElseThrow(
                    () -> new NotFoundException(translator, ACCOUNT_NOT_FOUND,
                        new Serializable[]{identifier}, identifier))
        ));
    }

    @GetMapping(value = API_ACCOUNTS, params = "name", produces = AccountVO.MEDIA_TYPE)
    public ResponseEntity<AccountVO> findByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(
            convertAndLinks(
                service.findByName(name).orElseThrow(
                    ()-> new NotFoundException(translator, ACCOUNT_NOT_FOUND,
                        new Serializable[]{name}, name))
        ));
    }

    private AccountVO convertAndLinks(Account account) {
        return addSelfLink(
                mapper.convertToVO(account)
        );
    }

    private List<AccountVO> convertAndLinks(List<Account> accounts) {
        return accounts.stream()
                .map(mapper::convertToVO)
                .map(this::addSelfLink)
                .toList();
    }

    private AccountVO addSelfLink(AccountVO result) {
        result.add(linkTo(methodOn(AccountController.class).findByPKey(result.getpKey())).withRel("accounts-findbypkey"));
        return result;
    }

    @GetMapping(API_ACCOUNTS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
            new Index(
                linkTo(methodOn(AccountController.class).findByPKey("pKey")).withRel("accounts-findbypkey"),
                linkTo(methodOn(AccountController.class).findByIdentifier("identifier")).withRel("accounts-findbyidentifier"),
                linkTo(methodOn(AccountController.class).findByName("name")).withRel("accounts-findbyname"),
                linkTo(methodOn(AccountController.class).findDefault()).withRel("accounts-finddefault"),
                linkTo(methodOn(AccountController.class).findAll()).withRel("accounts-findall")
            )
        );
    }
}
