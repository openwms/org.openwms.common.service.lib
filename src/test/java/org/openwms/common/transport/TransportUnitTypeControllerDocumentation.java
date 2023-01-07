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
package org.openwms.common.transport;

import org.ameba.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.TestData;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT_TYPES;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TransportUnitTypeControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TransportUnitTypeControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private AsyncTransactionApi transactionApi;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(API_TRANSPORT_UNIT_TYPES + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.transport-unit-types-findbypkey").exists())
                .andExpect(jsonPath("$._links.transport-unit-types-findtransportunittype").exists())
                .andExpect(jsonPath("$._links.transport-unit-types-findall").exists())
                .andExpect(jsonPath("$._links.length()", is(3)))
                .andDo(document("tut-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_findByPKey() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNIT_TYPES + "/1000"))
                .andExpect(jsonPath("$.pKey", is("1000")))
                .andExpect(status().isOk())
                .andDo(document("tut-find-bypkey"));
    }

    @Test void shall_findByPKey_404() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNIT_TYPES + "/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andDo(document("tut-find-bypkey-404"));
    }

    @Test void shall_findall() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNIT_TYPES))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(status().isOk())
                .andDo(document("tut-find-all"));
    }

    @Test void shall_findby_name() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNIT_TYPES)
                .queryParam("type", TestData.TUT_TYPE_PALLET))
                .andExpect(jsonPath("$.type", is("PALLET")))
                .andExpect(jsonPath("$.description", is("Euro pallet")))
                .andExpect(jsonPath("$.height", is("102")))
                .andExpect(jsonPath("$.width", is("80")))
                .andExpect(jsonPath("$.length", is("120")))
                .andExpect(status().isOk())
                .andDo(document("tut-find-type"));
    }

    @Test void shall_findby_name_404() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNIT_TYPES)
                .queryParam("type", "NOT_EXISTS"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(Messages.NOT_FOUND)))
                .andDo(document("tut-find-type-404"));
    }
}
