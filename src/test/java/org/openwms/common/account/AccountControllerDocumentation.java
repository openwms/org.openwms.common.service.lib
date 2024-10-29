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
package org.openwms.common.account;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.openwms.common.account.api.AccountApiConstants.API_ACCOUNTS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
                .andExpect(jsonPath("$._links.accounts-findbypkey").exists())
                .andExpect(jsonPath("$._links.accounts-finddefault").exists())
                .andExpect(jsonPath("$._links.accounts-findbyidentifier").exists())
                .andExpect(jsonPath("$._links.accounts-findbyname").exists())
                .andDo(document("acc-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_findByPKey() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS + "/1000"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.links[0].rel", is("accounts-findbypkey")))
//                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.pKey", is("1000")))
                .andExpect(jsonPath("$.identifier", is("D")))
                .andExpect(jsonPath("$.name", is("Default")))
                .andDo(document("acc-find-byPKey",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("_links").description("An array with hyperlinks to corresponding resources"),
                            fieldWithPath("_links.accounts-findbypkey").description("A link to get the resource by persistent key"),
                            fieldWithPath("_links.accounts-findbypkey.*").ignored(),
                            fieldWithPath("pKey").description("The persistent technical key of the Account"),
                            fieldWithPath("identifier").description("Unique natural key"),
                            fieldWithPath("name").description("Unique Account name")
                    )
                ));
    }

    @Test void shall_findByPKey_404() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS + "/2000"))
                .andExpect(status().isNotFound())
                .andDo(document("acc-find-byPKey404", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findByIdentifier() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                        .queryParam("identifier", "D"))
                .andExpect(status().isOk())
                .andDo(document("acc-find-byIdentifier", preprocessResponse(prettyPrint())));
    }

    @Test void shall_findByIdentifier_404() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS)
                        .queryParam("identifier", "UNKNOWN"))
                .andExpect(status().isNotFound())
                .andDo(document("acc-find-byIdentifier404", preprocessResponse(prettyPrint())));
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

    @Test void shall_findAll() throws Exception {
        mockMvc.perform(get(API_ACCOUNTS))
                .andExpect(status().isOk())
                .andDo(document("acc-find-all", preprocessResponse(prettyPrint())));
    }
}

