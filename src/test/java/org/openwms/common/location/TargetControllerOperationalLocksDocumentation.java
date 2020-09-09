/*
 * Copyright 2005-2020 the original author or authors.
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
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.location.api.LocationGroupMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openwms.common.location.api.LocationApiConstants.API_TARGETS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TargetControllerOperationalLocksDocumentation.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TargetControllerOperationalLocksDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private LocationGroupService locationGroupService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_olock_LocationGroup_IN() throws Exception {
        mockMvc.perform(post(API_TARGETS + "/IPOINT")
                .queryParam("reallocation", "true")
                .queryParam("type", "OPERATION_LOCK")
                .queryParam("mode", "IN"))
                .andExpect(status().isOk())
                .andDo(document("all-olock-in-lg-IPOINT"));
        LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
        assertThat(ipoint.isInfeedAllowed()).isTrue();
        assertThat(ipoint.isOutfeedAllowed()).isTrue();
        assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.OUTFEED);
        LocationGroup ipoint1 = locationGroupService.findByName("IPOINT1").orElseThrow(NotFoundException::new);
        assertThat(ipoint1.isInfeedAllowed()).isTrue();
        assertThat(ipoint1.isOutfeedAllowed()).isTrue();
        assertThat(ipoint1.getOperationMode()).isEqualTo(LocationGroupMode.OUTFEED);
    }

    @Test
    void shall_olock_LocationGroup_OUT() throws Exception {
        mockMvc.perform(post(API_TARGETS + "/IPOINT")
                .queryParam("type", "OPERATION_LOCK")
                .queryParam("mode", "OUT"))
                .andExpect(status().isOk())
                .andDo(document("all-olock-out-lg-IPOINT"));
        LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
        assertThat(ipoint.isInfeedAllowed()).isTrue();
        assertThat(ipoint.isOutfeedAllowed()).isTrue();
        assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED);
        LocationGroup ipoint1 = locationGroupService.findByName("IPOINT1").orElseThrow(NotFoundException::new);
        assertThat(ipoint1.isInfeedAllowed()).isTrue();
        assertThat(ipoint1.isOutfeedAllowed()).isTrue();
        assertThat(ipoint1.getOperationMode()).isEqualTo(LocationGroupMode.INFEED);
    }

    @Test
    void shall_olock_LocationGroup_INOUT() throws Exception {
        mockMvc.perform(post(API_TARGETS + "/IPOINT")
                .queryParam("type", "OPERATION_LOCK")
                .queryParam("mode", "IN_AND_OUT"))
                .andExpect(status().isOk())
                .andDo(document("all-olock-inout-lg-IPOINT"));
        LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
        assertThat(ipoint.isInfeedAllowed()).isTrue();
        assertThat(ipoint.isOutfeedAllowed()).isTrue();
        assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.NO_OPERATION);
    }

    @Test
    void shall_olock_LocationGroup_NONE() throws Exception {
        mockMvc.perform(post(API_TARGETS + "/IPOINT")
                .queryParam("type", "OPERATION_LOCK")
                .queryParam("mode", "NONE"))
                .andExpect(status().isOk())
                .andDo(document("all-olock-none-lg-IPOINT"));
        LocationGroup ipoint = locationGroupService.findByName("IPOINT").orElseThrow(NotFoundException::new);
        assertThat(ipoint.isInfeedAllowed()).isTrue();
        assertThat(ipoint.isOutfeedAllowed()).isTrue();
        assertThat(ipoint.getOperationMode()).isEqualTo(LocationGroupMode.INFEED_AND_OUTFEED);
    }
}
