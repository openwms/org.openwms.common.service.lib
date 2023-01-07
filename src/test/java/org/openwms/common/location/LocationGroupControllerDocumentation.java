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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.api.ErrorCodeVO;
import org.openwms.common.location.api.LocationApiConstants;
import org.openwms.common.location.api.LocationGroupMode;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.openwms.common.location.api.LocationApiConstants.API_LOCATION_GROUP;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationGroupControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@SuppressWarnings("squid:S3577")
@CommonApplicationTest
class LocationGroupControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LocationGroupService service;
    @MockBean
    private AsyncTransactionApi transactionApi;
    private MockMvc mockMvc;
    private RestDocumentationResultHandler documentationResultHandler;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        documentationResultHandler = document("lg/{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(LocationApiConstants.API_LOCATION_GROUPS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.location-groups-findbyname").exists())
                .andExpect(jsonPath("$._links.location-groups-findbynames").exists())
                .andExpect(jsonPath("$._links.location-groups-findall").exists())
                .andExpect(jsonPath("$._links.length()", is(5)))
                .andDo(documentationResultHandler.document(httpRequest(), httpResponse()))
        ;
    }

    /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("Finder Tests")
    class FinderTests {
     */
        @Test
        void shall_findby_name() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATION_GROUPS)
                    .queryParam("name", TestData.LOCATION_GROUP_NAME_LG2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("pKey").exists())
                    .andExpect(jsonPath("name", is(TestData.LOCATION_GROUP_NAME_LG2)))
                    .andExpect(jsonPath("parentName").exists())
                    .andExpect(jsonPath("accountId").exists())
                    .andExpect(jsonPath("operationMode", is(LocationGroupMode.INFEED_AND_OUTFEED)))
                    .andExpect(jsonPath("groupStateIn", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("groupStateOut", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("_links.parent.href").exists())
                    .andDo(
                            documentationResultHandler.document(
                                    requestParameters(
                                            parameterWithName("name").description("The unique name of the LocationGroup")
                                    ),
                                    responseFields(
                                            fieldWithPath("_links").ignored(),
                                            fieldWithPath("_links.parent").ignored(),
                                            fieldWithPath("_links.parent.href").ignored(),
                                            fieldWithPath("pKey").description("The persistent technical key of the LocationGroup"),
                                            fieldWithPath("name").description("Unique natural key"),
                                            fieldWithPath("accountId").description("The Account identifier the LocationGroup is assigned to"),
                                            fieldWithPath("parentName").description("Name of the parent LocationGroup"),
                                            fieldWithPath("operationMode").description("The operation mode is controlled by the subsystem and defines the physical mode a LocationGroup is currently able to operate in"),
                                            fieldWithPath("groupStateIn").description("State of infeed, controlled by the subsystem only"),
                                            fieldWithPath("groupStateOut").description("State of outfeed, controlled by the subsystem only")
                                    ),
                                    httpRequest(), httpResponse()
                            )
                    );
        }

        @Test
        void shall_findby_name_404() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATION_GROUPS)
                    .queryParam("name", "NOT_EXISTS"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_GROUP_NOT_FOUND)))
                    .andDo(documentationResultHandler.document(httpRequest(), httpResponse()));
        }

        @Test
        void shall_findby_names() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATION_GROUPS)
                    .queryParam("names", TestData.LOCATION_GROUP_NAME_LG2)
                    .queryParam("names", TestData.LOCATION_GROUP_NAME_LG3))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()", is(2)))
                    .andExpect(jsonPath("$[1].pKey").exists())
                    .andExpect(jsonPath("$[1].name", anyOf(is(TestData.LOCATION_GROUP_NAME_LG2), is(TestData.LOCATION_GROUP_NAME_LG3))))
                    .andExpect(jsonPath("$[1].accountId").exists())
                    .andExpect(jsonPath("$[1].parentName").exists())
                    .andExpect(jsonPath("$[1].operationMode", notNullValue()))
                    .andExpect(jsonPath("$[1].groupStateIn", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("$[1].groupStateOut", is(LocationGroupState.AVAILABLE.toString())))
                    .andExpect(jsonPath("$[1].links").exists())
                    .andDo(
                            documentationResultHandler.document(
                                    requestParameters(
                                            parameterWithName("names").description("A list of unique names to identify the LocationGroups")
                                    ),
                                    httpRequest(), httpResponse()
                            )
                    );
        }

        @Test
        void shall_findAll() throws Exception {
            mockMvc.perform(get(LocationApiConstants.API_LOCATION_GROUPS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andDo(documentationResultHandler.document(httpRequest(), httpResponse()));
        }
        /*
    }

         */

    /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("State Change Tests")
    class StateChangeTests {

     */
        @Test void shall_changeState_pkey_404() throws Exception {
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION_GROUPS)
                    .queryParam("name", "NOT_EXISTS")
                    .queryParam("op","change-state")
                    .content(mapper.writeValueAsString(ErrorCodeVO.UNLOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_GROUP_NOT_FOUND)))
                    .andDo(documentationResultHandler.document(httpRequest(), httpResponse()));
        }

        @Test void shall_change_state() throws Exception {
            mockMvc.perform(patch(LocationApiConstants.API_LOCATION_GROUPS)
                    .queryParam("name", TestData.LOCATION_GROUP_NAME_LG3)
                    .queryParam("op","change-state")
                    .content(mapper.writeValueAsString(ErrorCodeVO.LOCK_STATE_IN_AND_OUT))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            documentationResultHandler.document(
                                    requestParameters(
                                            parameterWithName("name").description("The unique name of the LocationGroup"),
                                            parameterWithName("op").description("The operation mode must be set to 'change-state'")
                                    ),
                                    httpRequest(), httpResponse()
                            )
                    );
            var lg = service.findByName(TestData.LOCATION_GROUP_NAME_LG3).get();
            assertThat(lg.getGroupStateIn()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
            assertThat(lg.getGroupStateOut()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
        }

        @Test void shall_change_state_pKey() throws Exception {
            var lg = service.findByName(TestData.LOCATION_GROUP_NAME_LG2).get();
            mockMvc.perform(
                    patch(API_LOCATION_GROUP + "/{pKey}", lg.getPersistentKey())
                    .queryParam("statein", LocationGroupState.NOT_AVAILABLE.toString())
                    .queryParam("stateout", LocationGroupState.NOT_AVAILABLE.toString())
                    .queryParam("op","change-state"))
                    .andExpect(status().isOk())
                    .andDo(
                            documentationResultHandler.document(
                                    pathParameters(
                                            parameterWithName("pKey").description("The persistent key of the LocationGroup")
                                    ),
                                    requestParameters(
                                            parameterWithName("statein").description("The infeed state to set"),
                                            parameterWithName("stateout").description("The outfeed state to set"),
                                            parameterWithName("op").description("The operation mode must be set to 'change-state'")
                                    ),
                                    httpRequest(), httpResponse()
                            )
                    );
            lg = service.findByName(TestData.LOCATION_GROUP_NAME_LG2).get();
            assertThat(lg.getGroupStateIn()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
            assertThat(lg.getGroupStateOut()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
        }
        /*
    }

         */
}
