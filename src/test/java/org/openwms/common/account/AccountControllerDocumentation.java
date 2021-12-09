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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.transactions.api.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.is;
import static org.openwms.common.account.api.AccountApiConstants.API_ACCOUNTS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A AccountControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class AccountControllerDocumentation {

    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;
    @MockBean
    private AsyncTransactionApi transactionApi;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation, WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(API_ACCOUNTS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.accounts-findall").exists())
                .andExpect(jsonPath("$._links.accounts-finddefault").exists())
                .andExpect(jsonPath("$._links.accounts-findbyidentifier").exists())
                .andExpect(jsonPath("$._links.accounts-findbyname").exists())
                .andDo(document("acc-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_findAll() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS))
                .andExpect(status().isOk())
                .andDo(document("acc-find-all", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findByIdentifier() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                .queryParam("identifier", "D"))
                .andExpect(status().isOk())
                .andDo(document("acc-find-byIdentifier", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findByName() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                .queryParam("name", "Default"))
                .andExpect(status().isOk())
                .andDo(document("acc-find-byName", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findByName_404() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                .queryParam("name", "UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(CommonMessageCodes.ACCOUNT_NOT_FOUND)))
                .andDo(document("acc-find-byName404", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findDefault() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                .queryParam("default", "true"))
                .andExpect(status().isOk())
                .andDo(document("acc-find-default", preprocessResponse(prettyPrint())));
    }

    @Transactional
    @Test void shall_findDefault_404() throws Exception {
        em.createNativeQuery("update com_account set c_default=false where c_pk=1000").executeUpdate();
        mockMvc.perform(get(API_ACCOUNTS)
                .queryParam("default", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(CommonMessageCodes.ACCOUNT_NO_DEFAULT)))
                .andDo(document("acc-find-default404", preprocessResponse(prettyPrint())));
        em.createNativeQuery("update com_account set c_default=true where c_pk=1000").executeUpdate();
    }
}

