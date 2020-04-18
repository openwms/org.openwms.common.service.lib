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

import org.ameba.http.MeasuredRestController;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.Index;
import org.openwms.common.account.api.AccountVO;
import org.openwms.core.http.AbstractWebController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static org.openwms.common.account.api.AccountApi.API_ACCOUNTS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A AccountController is a measured and Spring managed REST controller that serves Account information.
 *
 * @author Heiko Scherrer
 */
@MeasuredRestController
public class AccountController extends AbstractWebController {

    private final AccountService service;
    private final BeanMapper mapper;

    public AccountController(AccountService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(API_ACCOUNTS)
    public ResponseEntity<List<AccountVO>> findAll() {
        return ResponseEntity.ok(mapper.map(service.findAll(), AccountVO.class));
    }

    @GetMapping(API_ACCOUNTS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(AccountController.class).findAll()).withRel("accounts-findall")
                )
        );
    }
}
