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
package org.openwms.common.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ameba.exception.NotFoundException;
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
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATIONS;
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
                .andExpect(jsonPath("$._links.length()", is(9)))
                .andExpect(jsonPath("$._links.location-create").exists())
                .andExpect(jsonPath("$._links.location-updatelocation").exists())
                .andExpect(jsonPath("$._links.location-findbypkey").exists())
                .andExpect(jsonPath("$._links.location-findbycoordinate").exists())
                .andExpect(jsonPath("$._links.location-findbycoordinate-wc").exists())
                .andExpect(jsonPath("$._links.location-findbyerpcode").exists())
                .andExpect(jsonPath("$._links.location-findbyplccode").exists())
                .andExpect(jsonPath("$._links.location-forlocationgroup").exists())
                .andExpect(jsonPath("$._links.location-changestate").exists())
                .andDo(document("loc-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_create_Location() throws Exception {
        var location = new LocationVO("FGIN/PICK/WORK/0010/0000");
        location.setLocationGroupName("FGWORKPLACE9");
        location.setErpCode("PICK_10");
        location.setPlcCode("PICK_20");
        location.setType("PG");
        mockMvc.perform(
                post(LocationApiConstants.API_LOCATIONS)
                        .content(mapper.writeValueAsString(location))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(LocationVO.MEDIA_TYPE)
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
                                fieldWithPath("links[].*").ignored(),
                                fieldWithPath("ol").ignored(),
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

    @Nested
    @DisplayName("Find by PK Tests")
    class FindByPkTests {
        @Test void shall_findby_pKey() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS + "/" + TestData.LOCATION_PKEY_EXT)
                            .accept(LocationVO.MEDIA_TYPE)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andExpect(jsonPath("pKey", is(TestData.LOCATION_PKEY_EXT)))
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("erpCode", is(TestData.LOCATION_ERP_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-pKey", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_pKey_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS + "/UNKNOWN")
                            .accept(LocationVO.MEDIA_TYPE)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-pKey-404", preprocessResponse(prettyPrint())));
        }
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
        mockMvc.perform(
                put(LocationApiConstants.API_LOCATIONS)
                        .content(mapper.writeValueAsString(location))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(LocationVO.MEDIA_TYPE)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
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
                                fieldWithPath("links[].*").ignored(),
                                fieldWithPath("ol").ignored(),
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

    @Nested
    @DisplayName("ERP Code Tests")
    class ERPCodeTests {
        @Test void shall_findby_erpcode() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("erpCode", is(TestData.LOCATION_ERP_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-erp", preprocessResponse(prettyPrint())));
        }
    }

    @Nested
    @DisplayName("PLC Code Tests")
    class PLCCodeTests {

        @Test void shall_findby_plccode() throws Exception {
            mockMvc.perform(
                    get(LocationApiConstants.API_LOCATIONS)
                        .queryParam("plcCode", TestData.LOCATION_PLC_CODE_EXT)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("erpCode", is(TestData.LOCATION_ERP_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-plc", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_plccode_404() throws Exception {
            mockMvc.perform(
                    get(LocationApiConstants.API_LOCATIONS)
                            .queryParam("plcCode", "NOT EXISTS")
                            .accept(LocationVO.MEDIA_TYPE)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-plc-404", preprocessResponse(prettyPrint())));
        }

        @Test void shall_set_PLCState() throws Exception {
            var location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(patch(API_LOCATION + "/" + location.getPersistentKey())
                            .content(mapper.writeValueAsString(new ErrorCodeVO(31)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("op","change-state"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-plcstate", preprocessResponse(prettyPrint())));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.getPlcState()).isEqualTo(31);
        }
    }


        /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
         */
    @Nested
    @DisplayName("Find by ID Tests")
    class FindByIdTests {

        @Test void shall_findby_locationId() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationId", TestData.LOCATION_ID_EXT))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("locationId", is(TestData.LOCATION_ID_EXT)))
                    .andExpect(jsonPath("accountId").exists())
                    .andExpect(jsonPath("locationGroupName").exists())
                    .andExpect(jsonPath("plcCode", is(TestData.LOCATION_PLC_CODE_EXT)))
                    .andExpect(jsonPath("incomingActive", is(true)))
                    .andExpect(jsonPath("outgoingActive", is(true)))
                    .andExpect(jsonPath("plcState", is(0)))
                    .andDo(document("loc-find-coordinate", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_locationId_404() throws Exception {
            mockMvc.perform(
                    get(LocationApiConstants.API_LOCATIONS)
                            .queryParam("locationId", "EXT_/9999/9999/9999/9999")
                            .accept(LocationVO.MEDIA_TYPE)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_NOT_FOUND_BY_ID)))
                    .andDo(document("loc-find-coordinate-404", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_locationId_400() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationId", "INVALID_COORDINATE"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-400", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_locationId_wildcard() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("area", "FGIN")
                    .queryParam("aisle", "00__")
                    .queryParam("x", "LIFT")
                    .queryParam("y", "0000")
                    .queryParam("z", "%"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andExpect(jsonPath("$.length()", is(2)))
                    .andDo(document("loc-find-coordinate-wildcard", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_locationId_wildcard_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("area", "UNKN")
                    .queryParam("aisle", "%")
                    .queryParam("x", "%")
                    .queryParam("y", "%")
                    .queryParam("z", "%"))
                    .andExpect(status().isNotFound())
                    .andDo(document("loc-find-coordinate-wildcard-404", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_locationId_wildcard_All() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, LocationVO.MEDIA_TYPE))
                    .andDo(document("loc-find-coordinate-wildcard-all", preprocessResponse(prettyPrint())));
        }
    }

    @Nested
    @DisplayName("Find by name Tests")
    class FindByNameTests {
        @Test void shall_findby_lgname() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_lgnames() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG1)
                    .queryParam("locationGroupNames", TestData.LOCATION_GROUP_NAME_LG2))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-multiple", preprocessResponse(prettyPrint())))
                    .andExpect(jsonPath("$.length()", is(3)))
            ;
        }

        @Test void shall_findby_lgname_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", "NOT EXISTS"))
                    .andExpect(status().isOk())
                    .andDo(document("loc-find-in-lg-404", preprocessResponse(prettyPrint())));
        }

        @Test void shall_findby_lgname_wc() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATIONS)
                    .queryParam("locationGroupNames", "IP%"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    //.andExpect(jsonPath("$.length", is(2)))
                    .andDo(document("loc-find-in-lg-wc", preprocessResponse(prettyPrint())));
        }
    }

    @Nested
    @DisplayName("State Change Tests")
    class StateChangeTests {
        @Test void shall_changeState_pkey_404() throws Exception {
            mockMvc.perform(patch(API_LOCATION + "/NOTEXISTS")
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("op","change-state"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_NOT_FOUND_BY_PKEY)))
                    .andDo(document("loc-state-404", preprocessResponse(prettyPrint())));
        }

        @Test void shall_disable_Inbound_by_PK() throws Exception {
            var location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(
                    patch(API_LOCATION + "/" + location.getPersistentKey())
                        .queryParam("op","change-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ErrorCodeVO.LOCK_STATE_IN))
                    )
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-state-in", preprocessResponse(prettyPrint())));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.isInfeedBlocked()).isTrue();
        }

        @Test void shall_disable_Inbound_by_ID() throws Exception {
            var location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.isInfeedBlocked()).isFalse();
            mockMvc.perform(
                    patch(API_LOCATION)
                            .queryParam("locationId", TestData.LOCATION_ID_EXT)
                            .queryParam("op","change-state")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(ErrorCodeVO.LOCK_STATE_IN))
                    )
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-state-in-id", preprocessResponse(prettyPrint())));
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.isInfeedBlocked()).isTrue();
        }

        @Test void shall_set_both_states_by_PK() throws Exception {
            var location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            mockMvc.perform(
                    patch(API_LOCATION + "/" + location.getPersistentKey())
                        .queryParam("op","change-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ErrorCodeVO("******11",31)))
                    )
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-plcstate-both", preprocessResponse(prettyPrint())))
            ;
            location = service.findByLocationId(TestData.LOCATION_ID_EXT).get();
            assertThat(location.getPlcState()).isEqualTo(31);
            assertThat(location.isInfeedBlocked()).isTrue();
            assertThat(location.isOutfeedBlocked()).isTrue();
            assertThat(location.isInfeedActive()).isFalse();
            assertThat(location.isOutfeedActive()).isFalse();
        }

        @Test
        void shall_alock_Location_INOUT() throws Exception {
            mockMvc.perform(post(API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT)
                            .queryParam("type", "ALLOCATION_LOCK")
                            .queryParam("mode", "IN_AND_OUT"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-erpcode-lock"));
            Location loc = service.findByErpCode(TestData.LOCATION_ERP_CODE_EXT).orElseThrow(NotFoundException::new);
            assertThat(loc.isInfeedBlocked()).isTrue();
            assertThat(loc.isOutfeedBlocked()).isTrue();
        }

        @Test
        void shall_alock_Location_IN() throws Exception {
            mockMvc.perform(post(API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT)
                            .queryParam("type", "ALLOCATION_LOCK")
                            .queryParam("mode", "IN"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-erpcode-lock-in"));
            Location loc = service.findByErpCode(TestData.LOCATION_ERP_CODE_EXT).orElseThrow(NotFoundException::new);
            assertThat(loc.isInfeedBlocked()).isTrue();
            assertThat(loc.isOutfeedBlocked()).isFalse();
        }

        @Test
        void shall_alock_Location_OUT() throws Exception {
            mockMvc.perform(post(API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT)
                            .queryParam("type", "ALLOCATION_LOCK")
                            .queryParam("mode", "OUT"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-erpcode-lock-out"));
            Location loc = service.findByErpCode(TestData.LOCATION_ERP_CODE_EXT).orElseThrow(NotFoundException::new);
            assertThat(loc.isInfeedBlocked()).isFalse();
            assertThat(loc.isOutfeedBlocked()).isTrue();
        }

        @Test
        void shall_alock_Location_NONE() throws Exception {
            mockMvc.perform(post(API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT)
                            .queryParam("type", "ALLOCATION_LOCK")
                            .queryParam("mode", "NONE"))
                    .andExpect(status().isNoContent())
                    .andDo(document("loc-erpcode-lock"));
            Location loc = service.findByErpCode(TestData.LOCATION_ERP_CODE_EXT).orElseThrow(NotFoundException::new);
            assertThat(loc.isInfeedBlocked()).isFalse();
            assertThat(loc.isOutfeedBlocked()).isFalse();
        }

        @Test
        void operationLock_not_allowed_for_Locations() throws Exception {
            var res = mockMvc.perform(post(API_LOCATIONS)
                            .queryParam("erpCode", TestData.LOCATION_ERP_CODE_EXT)
                            .queryParam("type", "OPERATION_LOCK")
                            .queryParam("mode", "IN_AND_OUT"))
                    .andExpect(status().isInternalServerError())
                    .andDo(document("loc-erpcode-oplock"))
                    .andReturn();

            System.out.println(res);
        }
    }
}

