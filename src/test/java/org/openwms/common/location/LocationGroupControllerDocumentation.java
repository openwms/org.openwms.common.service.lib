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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ameba.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openwms.common.ApplicationTest;
import org.openwms.common.CommonConstants;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationGroupMode;
import org.openwms.common.location.api.LocationGroupState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationGroupControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@ApplicationTest
class LocationGroupControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LocationGroupService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Nested
    @DisplayName("Finder Tests")
    class FinderTests {
        @Test
        void shall_findby_name() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATION_GROUPS)
                    .param("name", TestData.LOCATION_GROUP_NAME_LG2))
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("name", is(TestData.LOCATION_GROUP_NAME_LG2)))
                    .andExpect(jsonPath("parent").exists())
                    .andExpect(jsonPath("operationMode", is(LocationGroupMode.INFEED_AND_OUTFEED)))
                    .andExpect(jsonPath("groupStateIn", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("groupStateOut", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("_links._parent.href").exists())
                    .andExpect(status().isOk())
                    .andDo(document("lg-find-name"));
        }

        @Test
        void shall_findby_name_404() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATION_GROUPS)
                    .param("name", "NOT_EXISTS"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(Messages.NOT_FOUND)))
                    .andDo(document("lg-find-name-404"));
        }

        @Test
        void shall_findby_names() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATION_GROUPS)
                    .param("names", TestData.LOCATION_GROUP_NAME_LG2)
                    .param("names", TestData.LOCATION_GROUP_NAME_LG3))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()", is(2)))
                    .andExpect(jsonPath("$[1].pKey").exists())
                    .andExpect(jsonPath("$[1].name", anyOf(is(TestData.LOCATION_GROUP_NAME_LG2), is(TestData.LOCATION_GROUP_NAME_LG3))))
                    .andExpect(jsonPath("$[1].parent").exists())
                    .andExpect(jsonPath("$[1].operationMode", notNullValue()))
                    .andExpect(jsonPath("$[1].groupStateIn", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("$[1].groupStateOut", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("$[1].links").exists())
                    .andDo(document("lg-find-names"));
        }

        @Test
        void shall_findAll() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATION_GROUPS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andDo(document("lg-find-all"));
        }
    }

    @Nested
    @DisplayName("State Change Tests")
    class StateChangeTests {
        @Test void shall_changeState_pkey_404() throws Exception {
            mockMvc.perform(patch(CommonConstants.API_LOCATION_GROUPS)
                    .param("name", "NOT_EXISTS")
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_GROUP_NOT_FOUND)))
                    .andDo(document("lg-state-404"));
        }

        @Test
        void shall_change_state() throws Exception {
            String pKey = service.findByName(TestData.LOCATION_GROUP_NAME_LG3).get().getPersistentKey();
            mockMvc.perform(patch(CommonConstants.API_LOCATION_GROUPS)
                    .param("name", TestData.LOCATION_GROUP_NAME_LG3)
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("lg-state"));
        }
    }
}
