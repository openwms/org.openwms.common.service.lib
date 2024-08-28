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
package org.openwms.common.transport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.openwms.common.transport.spi.NotApprovedException;
import org.openwms.common.transport.spi.TransportUnitMoveApproval;
import org.openwms.common.transport.spi.TransportUnitStateChangeApproval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.openwms.common.TestData.LOCATION_ID_EXT;
import static org.openwms.common.TestData.LOCATION_ID_FGIN0001LEFT;
import static org.openwms.common.TestData.TU_1_ID;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNITS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TransportUnitControllerWithMocksDocumentation is using mocks.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
@Import(TransportUnitControllerWithMocksDocumentation.ExtensionPointsConfiguration.class)
class TransportUnitControllerWithMocksDocumentation {

    private MockMvc mockMvc;
    @Autowired
    private TransportUnitService service;
    @MockBean
    private AsyncTransactionApi transactionApi;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation, WebApplicationContext context) {
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "0");
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @TestConfiguration
    public static class ExtensionPointsConfiguration {
        @Bean
        public TransportUnitMoveApproval moveApproval() {
            return (transportUnit, targetLocation) -> {
                throw new NotApprovedException("");
            };
        }

        @Bean
        public TransportUnitStateChangeApproval stateChangeApproval() {
            return (transportUnit, newState) -> {
                throw new NotApprovedException("");
            };
        }
    }

    @Test void shall_reject_state_change() throws Exception {
        // arrange
        var tu = service.findByBarcode(TU_1_ID);
        var stateChangeApproval = mock(TransportUnitStateChangeApproval.class);
        Mockito.doThrow(new NotApprovedException("Not allowed to change TransportUnit [%s] into state [%s]"
                        .formatted(TU_1_ID, "NOT_ACCEPTED")))
                .when(stateChangeApproval)
                .approve(tu, "NOT_ACCEPTED");

        assertThat(tu.getState()).isEqualTo(TransportUnitState.AVAILABLE.name());

        // act
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                        .queryParam("bk", TU_1_ID)
                        .queryParam("state", "NOT_ACCEPTED"))
                .andDo(document("tu-state-change-na",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("state").description("The new state the TransportUnit shall be changed to")
                        )
                ))
                .andExpect(status().isConflict())
        ;

        // assert
        tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getState()).isEqualTo("AVAILABLE");
    }

    @Test void shall_reject_move() throws Exception {
        // arrange
        var tu = service.findByBarcode(TU_1_ID);
        var moveApproval = mock(TransportUnitMoveApproval.class);
        Mockito.doThrow(new NotApprovedException("Not allowed to move TransportUnit [%s] to Location [%s]"
                        .formatted(TU_1_ID, LOCATION_ID_FGIN0001LEFT)))
                .when(moveApproval)
                .approve(any(), any());

        assertThat(tu.getActualLocation().getLocationId()).hasToString(LOCATION_ID_EXT);

        // act
        mockMvc.perform(patch(API_TRANSPORT_UNITS)
                        .queryParam("bk", TU_1_ID)
                        .queryParam("newLocation", LOCATION_ID_FGIN0001LEFT))
                .andDo(document("tu-move-na",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("newLocation").description("The new Location where the TransportUnit shall be moved to")
                        )
                ))
                .andExpect(status().isConflict())
        ;

        // assert
        tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getActualLocation().getLocationId()).hasToString(LOCATION_ID_EXT);
    }
}
