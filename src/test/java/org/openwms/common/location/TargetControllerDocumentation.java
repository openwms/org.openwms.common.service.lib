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
package org.openwms.common.location;

import org.ameba.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.api.LocationGroupMode;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.openwms.common.location.api.LocationApiConstants.API_TARGETS;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TargetControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@SuppressWarnings("squid:S3577")
@CommonApplicationTest
class TargetControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private LocationGroupService locationGroupService;
    @MockBean
    private AsyncTransactionApi transactionApi;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    /* Depends on https://github.com/spring-projects/spring-framework/issues/19930
    @Nested
    @DisplayName("Allocation Locks")
    class AllocationLockTests {
     */
        @Test
        void shall_lock_LocationGroup_IN() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("type", "ALLOCATION_LOCK")
                    .queryParam("mode", "IN"))
                    .andExpect(status().isOk())
                    .andDo(document("all-lock-in-lg-IPOINT"));
            LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.isInfeedAllowed()).isFalse();
            assertThat(ipoint.isOutfeedAllowed()).isTrue();
            assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED_AND_OUTFEED);
            LocationGroup ipoint1 = locationGroupService.findByName("IPOINT1").orElseThrow(NotFoundException::new);
            assertThat(ipoint1.isInfeedAllowed()).isFalse();
            assertThat(ipoint1.isOutfeedAllowed()).isTrue();
        }

        @Test
        void shall_lock_LocationGroup_OUT() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("type", "ALLOCATION_LOCK")
                    .queryParam("mode", "OUT"))
                    .andExpect(status().isOk())
                    .andDo(document("all-lock-out-lg-IPOINT"));
            LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.isInfeedAllowed()).isTrue();
            assertThat(ipoint.isOutfeedAllowed()).isFalse();
            assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED_AND_OUTFEED);
            LocationGroup ipoint1 = locationGroupService.findByName("IPOINT1").orElseThrow(NotFoundException::new);
            assertThat(ipoint1.isInfeedAllowed()).isTrue();
            assertThat(ipoint1.isOutfeedAllowed()).isFalse();
        }

        @Test
        void shall_lock_LocationGroup_INOUT() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("type", "ALLOCATION_LOCK")
                    .queryParam("mode", "IN_AND_OUT"))
                    .andExpect(status().isOk())
                    .andDo(document("all-lock-inout-lg-IPOINT"));
            LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED_AND_OUTFEED);
            assertThat(ipoint.isInfeedAllowed()).isFalse();
            assertThat(ipoint.isOutfeedAllowed()).isFalse();
        }

        @Test
        void shall_lock_LocationGroup_NONE() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("type", "ALLOCATION_LOCK")
                    .queryParam("mode", "NONE"))
                    .andExpect(status().isOk())
                    .andDo(document("all-lock-none-lg-IPOINT"));
            LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED_AND_OUTFEED);
            assertThat(ipoint.isInfeedAllowed()).isTrue();
            assertThat(ipoint.isOutfeedAllowed()).isTrue();
        }
        /*
    }

         */

    @Nested
    @DisplayName("Permanent Locks")
    class PermanentLockTests {
        @Test void shall_lock_LocationGroup() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("reallocation", "true")
                    .queryParam("type", "PERMANENT_LOCK")
                    .queryParam("mode", "lock"))
                    .andExpect(status().isOk())
                    .andDo(document("lock-lg-IPOINT",
                            queryParameters(
                                    parameterWithName("reallocation").description("*true* to trigger an order re-allocation, or *false* if not"),
                                    parameterWithName("type").description("The lock type"),
                                    parameterWithName("mode").description("The operation mode")
                            ),
                            httpRequest(), httpResponse()
                    ));

            var ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.isInfeedAllowed()).isFalse();
            assertThat(ipoint.isOutfeedAllowed()).isFalse();
            assertThat(ipoint.getOperationMode()).isEqualTo("NO_OPERATION");

            var ipoint1 = locationGroupService.findByName("IPOINT1").orElseThrow(NotFoundException::new);
            assertThat(ipoint1.isInfeedAllowed()).isFalse();
            assertThat(ipoint1.isOutfeedAllowed()).isFalse();
            assertThat(ipoint1.getOperationMode()).isEqualTo("NO_OPERATION");
        }

        @Test void shall_lock_unknown_LocationGroup() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/FOO")
                    .queryParam("type", "PERMANENT_LOCK")
                    .queryParam("mode", "lock"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_GROUP_NOT_FOUND)))
                    .andExpect(jsonPath("httpStatus", is("404")))
                    .andDo(document("lock-lg-unknown"));
        }

        @Test void shall_unlock_LocationGroup() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/IPOINT")
                    .queryParam("type", "PERMANENT_LOCK")
                    .queryParam("mode", "unlock"))
                    .andExpect(status().isOk())
                    .andDo(document("unlock-lg-IPOINT"));
            LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
            assertThat(ipoint.isInfeedAllowed()).isTrue();
            assertThat(ipoint.isOutfeedAllowed()).isTrue();
            assertThat(ipoint.getOperationMode()).isEqualTo("INFEED_AND_OUTFEED");
        }

        @Test void shall_unlock_unknown_LocationGroup() throws Exception {
            mockMvc.perform(post(API_TARGETS + "/FOO")
                    .queryParam("type", "PERMANENT_LOCK")
                    .queryParam("mode", "unlock"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("messageKey", is(CommonMessageCodes.LOCATION_GROUP_NOT_FOUND)))
                    .andExpect(jsonPath("httpStatus", is("404")))
                    ;
        }
    }
}
