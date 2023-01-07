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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.openwms.common.transport.api.TransportUnitTypeVO;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.barcode.BarcodeGenerator;
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
import static org.openwms.common.TestData.TU_1_ID;
import static org.openwms.common.TestData.TU_1_PKEY;
import static org.openwms.common.TestData.TU_2_ID;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNIT;
import static org.openwms.common.transport.api.TransportApiConstants.API_TRANSPORT_UNITS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TransportUnitControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@CommonApplicationTest
class TransportUnitControllerDocumentation {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private TransportUnitService service;
    @Autowired
    private BarcodeGenerator generator;
    @MockBean
    private AsyncTransactionApi transactionApi;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation, WebApplicationContext context) {
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "0");
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(API_TRANSPORT_UNITS + "/index")
                )
                .andDo(document("tu-index", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.transport-unit-createtuwithbody").exists())
                .andExpect(jsonPath("$._links.transport-unit-createtuwithparams").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbypkey").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcode").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcodes").exists())
                .andExpect(jsonPath("$._links.transport-unit-findonlocation").exists())
                .andExpect(jsonPath("$._links.transport-unit-block").exists())
                .andExpect(jsonPath("$._links.transport-unit-unblock").exists())
                .andExpect(jsonPath("$._links.length()", is(9)))
        ;
    }

    @Test void shall_create_with_barcode_generation() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET))
                .andDo(document("tu-create-generate", preprocessResponse(prettyPrint())))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;

        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;
    }

    @Test void shall_createSimple() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET)
                .queryParam("strict", "false"))
                .andDo(document("tu-create-simple",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("actualLocation").description("The Location where to book on the TransportUnit initially"),
                                parameterWithName("tut").description("The name of the TransportUnitType assigned to the TransportUnit"),
                                parameterWithName("strict").description("If true, the service fails if the TransportUnit already exist, if false it does not fail and returns the existing one")
                        )
                ))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;
    }

    @Test void shall_createSimple_with_error() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", TU_1_ID)
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET)
                .queryParam("strict", "true"))
                .andDo(document("tu-create-simple-error", preprocessResponse(prettyPrint())))
                .andExpect(status().isConflict())
        ;
    }

    @Test void shall_createFull() throws Exception {
        var tut = TransportUnitTypeVO.Builder.aTransportUnitTypeVO().withType(TestData.TUT_TYPE_PALLET).build();
        var transportUnit = new TransportUnitVO("4710", tut, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .queryParam("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("tu-create-full",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("strict").description("If true, the service fails if the TransportUnit already exist, if false it does not fail and returns the existing one")
                        )
                ))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;
    }

    @Test void shall_create_with_invalid() throws Exception {
        var tut = TransportUnitTypeVO.Builder.aTransportUnitTypeVO().withType(TestData.TUT_TYPE_PALLET).build();
        var transportUnit = new TransportUnitVO("4711", tut, new LocationVO(TestData.LOCATION_ID_EXT));
        transportUnit.setActualLocation(null);
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", TU_1_ID)
                .queryParam("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("tu-create-invalid", preprocessResponse(prettyPrint())))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test void shall_create_with_error() throws Exception {
        var tut = TransportUnitTypeVO.Builder.aTransportUnitTypeVO().withType(TestData.TUT_TYPE_PALLET).build();
        var transportUnit = new TransportUnitVO("4711", tut, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", TU_1_ID)
                .queryParam("strict", "true")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("tu-create-error", preprocessResponse(prettyPrint())))
                .andExpect(status().isConflict())
        ;
    }

    @Test void shall_update_existing() throws Exception {
        var transportUnit = createValidTU(TU_1_ID, TU_1_PKEY, TestData.LOCATION_ID_FGIN0001LEFT);
        mockMvc.perform(put(API_TRANSPORT_UNITS)
                .queryParam("bk", TU_1_ID)
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("tu-update",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isOk())
        ;
        var tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getActualLocation().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_FGIN0001LEFT));
    }

    private TransportUnitVO createValidTU(String barcode, String pKey, String actualLocation) {
        var tut = new TransportUnitTypeVO("PL");
        var transportUnit = new TransportUnitVO(barcode);
        transportUnit.setActualLocation(new LocationVO(actualLocation));
        transportUnit.setState("AVAILABLE");
        transportUnit.setTransportUnitType(tut);
        transportUnit.setpKey(pKey);
        return transportUnit;
    }

    @Test void shall_update_404() throws Exception {
        var transportUnit = createValidTU("00000000000000004710", "UNKNOWN", TestData.LOCATION_ID_FGIN0001LEFT);
        mockMvc.perform(put(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("tu-update-404",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isNotFound())
        ;
    }

    @Test void shall_move() throws Exception {
        mockMvc.perform(
                patch(API_TRANSPORT_UNITS)
                        .queryParam("bk", TU_1_ID)
                        .queryParam("newLocation", TestData.LOCATION_ID_FGIN0001LEFT)
                )
                .andDo(document("tu-move",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("newLocation").description("The target Location where to move the TransportUnit to")
                        )
                ))
                .andExpect(status().isOk())
        ;
    }

    @Test void shall_add_error() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNIT + "/error")
                .queryParam("bk", TU_1_ID)
                .queryParam("errorCode", "bla"))
                .andDo(document("tu-add-error",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("errorCode").description("The error text")
                        )
                ))
                .andExpect(status().isNoContent())
        ;
    }

    @Test void shall_findAll() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS))
                .andDo(document("tu-find-all", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openwms.transport-unit-v1+json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(2)))
                ;
    }

    @Test void shall_findByPKey() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS + "/" + TU_1_PKEY))
                .andDo(document("tu-find-by-pkey", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openwms.transport-unit-v1+json"))
        ;
    }

    @Test void shall_findByBarcode() throws Exception {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "0");
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", TU_1_ID))
                .andDo(document("tu-find-by-barcode",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isOk())
        ;
    }

    @Test void shall_findByBarcode_short() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", "4711"))
                .andDo(document("tu-find-by-barcode-short",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isOk())
        ;
    }

    @Test void shall_findByBarcode_404() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", "999"))
                .andDo(document("tu-find-by-barcode-404", preprocessResponse(prettyPrint())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(CommonMessageCodes.TU_BARCODE_NOT_FOUND)))
        ;
    }

    @Test void shall_findByBarcodes() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bks", TU_1_ID)
                .queryParam("bks", TU_2_ID)
                .queryParam("bks", "00000000000000004713"))
                .andDo(document("tu-find-by-barcodes",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bks").description("A set of identifying Barcodes of the TransportUnit to search for")
                                )
                ))
                .andExpect(status().isOk())
        ;
    }

    @Test void shall_findOnLocation() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", "FGIN/IPNT/0001/0000/0000"))
                .andDo(document("tu-find-on-location",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("actualLocation").description("The Location to find all TransportUnits booked on")
                                )
                ))
                .andExpect(status().isOk())
        ;
    }

    @Test void shall_block_and_unblock_TU() throws Exception {
        var tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getState()).isEqualTo(TransportUnitState.AVAILABLE);

        mockMvc.perform(post(API_TRANSPORT_UNITS + "/block")
                .queryParam("bk", TU_1_ID))
                .andDo(document("tu-block",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isNoContent())
        ;
        
        tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getState()).isEqualTo(TransportUnitState.BLOCKED);

        mockMvc.perform(post(API_TRANSPORT_UNITS + "/available")
                .queryParam("bk", TU_1_ID))
                .andDo(document("tu-unblock",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                                )
                ))
                .andExpect(status().isNoContent())
        ;

        tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getState()).isEqualTo(TransportUnitState.AVAILABLE);

        // Set to quality-check
        mockMvc.perform(post(API_TRANSPORT_UNITS + "/quality-check")
                        .queryParam("bk", TU_1_ID))
                .andDo(document("tu-quality-check",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ))
                .andExpect(status().isNoContent())
        ;

        tu = service.findByBarcode(TU_1_ID);
        assertThat(tu.getState()).isEqualTo(TransportUnitState.QUALITY_CHECK);
    }
}
