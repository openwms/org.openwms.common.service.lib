/*
 * Copyright 2005-2019 the original author or authors.
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
package org.openwms.common.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.ApplicationTest;
import org.openwms.common.CommonConstants;
import org.openwms.common.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@ApplicationTest
class LocationControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_findby_plccode() throws Exception {
        mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                .param("plcCode", TestData.LOCATION_PLC_CODE_EXT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("pKey").exists())
                .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                .andExpect(jsonPath("locationGroupName").exists())
                .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                .andExpect(jsonPath("incomingActive", is(true)))
                .andExpect(jsonPath("outgoingActive", is(true)))
                .andExpect(jsonPath("plcState", is(0)))
                .andDo(document("loc-find-plc"));
    }

    @Test
    void shall_findby_plccode_404() throws Exception {
        mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                .param("plcCode", "NOT EXISTS"))
                .andExpect(status().isNotFound())
                .andDo(document("loc-find-plc-404"));
    }
}
