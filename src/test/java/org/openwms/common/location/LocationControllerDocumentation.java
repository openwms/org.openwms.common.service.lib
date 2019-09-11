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
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonConstants;
import org.openwms.common.TestData;
import org.openwms.common.location.api.ErrorCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class LocationControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LocationService service;
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
                        get(CommonConstants.API_LOCATIONS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.location-findbycoordinate").exists())
                .andExpect(jsonPath("$._links.location-findbyplccode").exists())
                .andExpect(jsonPath("$._links.location-forlocationgroup").exists())
                .andExpect(jsonPath("$._links.location-changestate").exists())
                .andExpect(jsonPath("$._links.length()", is(4)))
                .andDo(document("loc-index", preprocessResponse(prettyPrint())))
        ;
    }

    /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("PLC Code Tests")
    class PLCCodeTests {

     */
        @Test void shall_findby_plccode() throws Exception {
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

        @Test void shall_findby_plccode_404() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("plcCode", "NOT EXISTS"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-plc-404"));
        }
        /*
    }

         */

        /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("Coordinate Tests")
    class CoordinateTests {

         */
        @Test void shall_findby_locationPk() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationPK", TestData.LOCATION_ID_EXT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-coordinate"));
        }

        @Test void shall_findby_locationPk_404() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationPK", "EXT_/9999/9999/9999/9999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(Messages.NOT_FOUND)))
                    .andDo(document("loc-find-coordinate-404"));
        }

        @Test void shall_findby_locationPk_400() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationPK", "NOT EXISTS"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-400"));
        }

        @Test void shall_findby_locationPk_wildcard() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("area", "FGIN")
                    .param("aisle", "00__")
                    .param("x", "LIFT")
                    .param("y", "0000")
                    .param("z", "%"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(2)))
                    .andDo(document("loc-find-coordinate-wildcard"));
        }

        @Test void shall_findby_locationPk_wildcard_404() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("area", "UNKN")
                    .param("aisle", "%")
                    .param("x", "%")
                    .param("y", "%")
                    .param("z", "%"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-wildcard-404"));
        }
        /*
    }

         */

    @Nested
    @DisplayName("LocationGroup Tests")
    class LGTests {
        @Test void shall_findby_lgname() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg"));
        }

        @Test void shall_findby_lgnames() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1)
                    .param("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG2))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-multiple"));
        }

        @Test void shall_findby_lgname_404() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationGroupNames", "NOT EXISTS"))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-404"));
        }

        @Test void shall_findby_lgname_wc() throws Exception {
            mockMvc.perform(get(CommonConstants.API_LOCATIONS)
                    .param("locationGroupNames", "IP%"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    //.andExpect(jsonPath("$.length", is(2)))
                    .andDo(document("loc-find-in-lg-wc"));
        }
    }

        /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("State Change Tests")
    class StateChangeTests {

         */
        @Test void shall_changeState_pkey_404() throws Exception {
            mockMvc.perform(patch(CommonConstants.API_LOCATION + "/NOTEXISTS")
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("op","change-state"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(Messages.NOT_FOUND)))
                    .andDo(document("loc-state-404"));
        }

        @Test void shall_disable_Inbound_() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(CommonConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(ErrorCodeVO.LOCK_STATE_IN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("op","change-state"))
                    .andExpect(status().isOk())
                    .andDo(document("loc-state-in"));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.isInfeedBlocked()).isTrue();
        }

        @Test void shall_set_PLCState() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(CommonConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(new ErrorCodeVO(31)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("op","change-state"))
                    .andExpect(status().isOk())
                    .andDo(document("loc-plcstate"));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.getPlcState()).isEqualTo(31);
        }

        @Test void shall_set_both_states() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(CommonConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(new ErrorCodeVO("******11",31)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("op","change-state"))
                    .andExpect(status().isOk());
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.getPlcState()).isEqualTo(31);
            assertThat(location.isInfeedBlocked()).isTrue();
            assertThat(location.isOutfeedBlocked()).isTrue();
            assertThat(location.isInfeedActive()).isFalse();
            assertThat(location.isOutfeedActive()).isFalse();
        }
        /*
    }

         */
}

