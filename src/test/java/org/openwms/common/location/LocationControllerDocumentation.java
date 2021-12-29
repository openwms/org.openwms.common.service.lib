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
package org.openwms.common.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationApiConstants;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@SuppressWarnings("squid:S3577")
@CommonApplicationTest
class LocationControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LocationService service;
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
                        get(LocationApiConstants.API_LOCATIONS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.location-changestate").exists())
                .andExpect(jsonPath("$._links.location-create").exists())
                .andExpect(jsonPath("$._links.location-findbycoordinate").exists())
                .andExpect(jsonPath("$._links.location-findbyerpcode").exists())
                .andExpect(jsonPath("$._links.location-findbyplccode").exists())
                .andExpect(jsonPath("$._links.location-fortuple").exists())
                .andExpect(jsonPath("$._links.location-forlocationgroup").exists())
                .andExpect(jsonPath("$._links.location-updatelocation").exists())
                .andExpect(jsonPath("$._links.length()", is(8)))
                .andDo(document("loc-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_create_Location() throws Exception {
        var location = new LocationVO("FGIN/PICK/WORK/0010/0000");
        location.setLocationGroupName("FGWORKPLACE9");
        location.setErpCode("PICK_10");
        location.setPlcCode("PICK_20");
        location.setType("PG");
        mockMvc.perform(post(LocationApiConstants.API_LOCATIONS)
                        .content(mapper.writeValueAsString(location))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.locationId", is(location.getLocationId())))
                .andExpect(jsonPath("$.plcCode", is("PICK_20")))
                .andExpect(jsonPath("$.erpCode", is("PICK_10")))
                .andExpect(jsonPath("$.incomingActive", is(true)))
                .andExpect(jsonPath("$.outgoingActive", is(true)))
                .andExpect(jsonPath("$.plcState", is(0)))
                .andExpect(jsonPath("$.type", is("PG")))
                .andExpect(jsonPath("$.locationGroupName", is("FGWORKPLACE9")))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("loc-created",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("links[]").ignored(),
                                fieldWithPath("locationId").description("Unique natural key"),
                                fieldWithPath("plcCode").description("PLC code of the Location"),
                                fieldWithPath("erpCode").description("ERP code of the Location"),
                                fieldWithPath("type").description("The name of the LocationType the Location belongs to"),
                                fieldWithPath("locationGroupName").description("The LocationGroup the Location belongs to")
                        ),
                        responseFields(
                                fieldWithPath("pKey").description("The persistent technical key of the Location"),
                                fieldWithPath("locationId").description("Unique natural key"),
                                fieldWithPath("plcCode").description("PLC code of the Location"),
                                fieldWithPath("erpCode").description("ERP code of the Location"),
                                fieldWithPath("incomingActive").description("Whether the Location is enabled for incoming movements (read-only)"),
                                fieldWithPath("outgoingActive").description("Whether the Location is enabled for outgoing movements (read-only)"),
                                fieldWithPath("plcState").description("The current state, set by the PLC system (read-only)"),
                                fieldWithPath("type").description("The name of the LocationType the Location belongs to"),
                                fieldWithPath("locationGroupName").description("The LocationGroup the Location belongs to")
                        )
                ));
    }

    @Test void shall_update_Location() throws Exception {
        var location = new LocationVO("FGIN/CONV/0001/0000/0000");
        location.setpKey("1000");
        location.setLocationGroupName("FGWORKPLACE9");
        location.setErpCode("PICK_10");
        location.setPlcCode("PICK_20");
        location.setPlcState(21);
        location.setAccountId("A1");
        location.setIncomingActive(false);
        location.setOutgoingActive(false);
        location.setSortOrder(99);
        location.setStockZone("STOCK");
        location.setType("FG");
        mockMvc.perform(put(LocationApiConstants.API_LOCATIONS)
                        .content(mapper.writeValueAsString(location))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pKey", is("1000")))
                .andExpect(jsonPath("$.locationId", is(location.getLocationId())))
                .andExpect(jsonPath("$.accountId", is("A1")))
                .andExpect(jsonPath("$.plcCode", is("PICK_20")))
                .andExpect(jsonPath("$.erpCode", is("PICK_10")))
                .andExpect(jsonPath("$.sortOrder", is(99)))
                .andExpect(jsonPath("$.stockZone", is("STOCK")))
                .andExpect(jsonPath("$.incomingActive", is(true))) // not allowed to change this here
                .andExpect(jsonPath("$.outgoingActive", is(true))) // not allowed to change this here
                .andExpect(jsonPath("$.plcState", is(0))) // not allowed to change this here
                .andExpect(jsonPath("$.type", is("FG")))
                .andExpect(jsonPath("$.locationGroupName", is("FGWORKPLACE9")))
                .andDo(document("loc-updated",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("links[]").ignored(),
                                fieldWithPath("pKey").description("The persistent technical key of the Location"),
                                fieldWithPath("locationId").description("Unique natural key"),
                                fieldWithPath("accountId").description("The ID of the Account, the Location is assigned to"),
                                fieldWithPath("plcCode").description("PLC code of the Location"),
                                fieldWithPath("erpCode").description("ERP code of the Location"),
                                fieldWithPath("sortOrder").description("Sort order index used by strategies for putaway, or picking"),
                                fieldWithPath("stockZone").description("Might be assigned to a particular zone in stock"),
                                fieldWithPath("incomingActive").description("Whether the Location is enabled for incoming movements (read-only)"),
                                fieldWithPath("outgoingActive").description("Whether the Location is enabled for outgoing movements (read-only)"),
                                fieldWithPath("plcState").description("The current state, set by the PLC system (read-only)"),
                                fieldWithPath("type").description("The name of the LocationType the Location belongs to"),
                                fieldWithPath("locationGroupName").description("The LocationGroup the Location belongs to")
                        ),
                        responseFields(
                                fieldWithPath("pKey").description("The persistent technical key of the Location"),
                                fieldWithPath("locationId").description("Unique natural key"),
                                fieldWithPath("accountId").description("The ID of the Account, the Location is assigned to"),
                                fieldWithPath("plcCode").description("PLC code of the Location"),
                                fieldWithPath("erpCode").description("ERP code of the Location"),
                                fieldWithPath("sortOrder").description("Sort order index used by strategies for putaway, or picking"),
                                fieldWithPath("stockZone").description("Might be assigned to a particular zone in stock"),
                                fieldWithPath("incomingActive").description("Whether the Location is enabled for incoming movements (read-only)"),
                                fieldWithPath("outgoingActive").description("Whether the Location is enabled for outgoing movements (read-only)"),
                                fieldWithPath("plcState").description("The current state, set by the PLC system (read-only)"),
                                fieldWithPath("type").description("The name of the LocationType the Location belongs to"),
                                fieldWithPath("locationGroupName").description("The LocationGroup the Location belongs to")
                        )
                ));
    }

    /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("PLC Code Tests")
    class PLCCodeTests {

     */
        @Test void shall_findby_erpcode() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("erpCode", is(TestData.LOCATION_ERP_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-erp"));
        }

        @Test void shall_findby_plccode() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("plcCode", TestData.LOCATION_PLC_CODE_EXT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("erpCode", is(TestData.LOCATION_ERP_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-plc"));
        }

        @Test void shall_findby_plccode_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("plcCode", "NOT EXISTS"))
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
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationPK", TestData.LOCATION_ID_EXT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("accountId").exists())
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-coordinate"));
        }

        @Test void shall_findby_locationPk_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationPK", "EXT_/9999/9999/9999/9999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_NOT_FOUND)))
                    .andDo(document("loc-find-coordinate-404"));
        }

        @Test void shall_findby_locationPk_400() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationPK", "INVALID_COORDINATE"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-400"));
        }

        @Test void shall_findby_locationPk_wildcard() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("area", "FGIN")
                    .queryParam("aisle", "00__")
                    .queryParam("x", "LIFT")
                    .queryParam("y", "0000")
                    .queryParam("z", "%"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(2)))
                    .andDo(document("loc-find-coordinate-wildcard"));
        }

        @Test void shall_findby_locationPk_wildcard_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("area", "UNKN")
                    .queryParam("aisle", "%")
                    .queryParam("x", "%")
                    .queryParam("y", "%")
                    .queryParam("z", "%"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-wildcard-404"));
        }

        @Test void shall_findby_locationPk_wildcard_All() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS))
                    .andExpect(status().isOk());
        }
        /*
    }

         */

    @Nested
    @DisplayName("LocationGroup Tests")
    class LGTests {
        @Test void shall_findby_lgname() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg"));
        }

        @Test void shall_findby_lgnames() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG2))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-multiple"));
        }

        @Test void shall_findby_lgname_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", "NOT EXISTS"))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-404"));
        }

        @Test void shall_findby_lgname_wc() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", "IP%"))
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
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION + "/NOTEXISTS")
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("op","change-state"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_NOT_FOUND)))
                    .andDo(document("loc-state-404"));
        }

        @Test void shall_disable_Inbound_() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(ErrorCodeVO.LOCK_STATE_IN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("op","change-state"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-state-in"));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.isInfeedBlocked()).isTrue();
        }

        @Test void shall_set_PLCState() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(new ErrorCodeVO(31)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("op","change-state"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-plcstate"));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.getPlcState()).isEqualTo(31);
        }

        @Test void shall_set_both_states() throws Exception {
            Location location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION + "/" + location.getPersistentKey())
                    .content(mapper.writeValueAsString(new ErrorCodeVO("******11",31)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("op","change-state"))
                    .andExpect(status().isNoContent());
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

