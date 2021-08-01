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
package org.openwms.common.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.LocationPK;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.transport.api.TransportUnitVO;
import org.openwms.common.transport.barcode.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
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
@TestPropertySource(properties = {
        "owms.common.barcode.padder = 0",
        "owms.common.barcode.pattern = %1$20s",
        "org.openwms.common.transport.BarcodeFormatProvider=org.openwms.common.transport.ConfiguredBarcodeFormat"
})
class TransportUnitControllerDocumentation {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private TransportUnitService service;
    @Autowired
    private BarcodeGenerator generator;

    @BeforeClass void onBeforeClass() {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "0");
    }

    @AfterClass void onAfterClass() {
        System.clearProperty("owms.common.barcode.pattern");
        System.clearProperty("owms.common.barcode.padder");
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation, WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(API_TRANSPORT_UNITS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.transport-unit-findbypkey").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcode").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcodes").exists())
                .andExpect(jsonPath("$._links.transport-unit-findonlocation").exists())
                .andExpect(jsonPath("$._links.length()", is(4)))
                .andDo(document("tu-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_create_with_barcode_generation() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tu-create-generate"));

        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test void shall_createSimple() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET)
                .queryParam("strict", "false"))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tu-create-simple",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("actualLocation").description("The Location where to book on the TransportUnit initially"),
                                parameterWithName("tut").description("The name of the TransportUnitType assigned to the TransportUnit"),
                                parameterWithName("strict").description("If true, the service fails if the TransportUnit already exist, if false it does not fail and returns the existing one")
                        )
                ));
    }

    @Test void shall_createSimple_with_error() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004711")
                .queryParam("actualLocation", TestData.LOCATION_ID_EXT)
                .queryParam("tut", TestData.TUT_TYPE_PALLET)
                .queryParam("strict", "true"))
                .andExpect(status().isConflict())
                .andDo(document("tu-create-simple-error"));
    }

    @Test void shall_createFull() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4710", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .queryParam("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tu-create-full",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("strict").description("If true, the service fails if the TransportUnit already exist, if false it does not fail and returns the existing one")
                        )
                ));
    }

    @Test void shall_create_with_invalid() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4711", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        transportUnit.setActualLocation(null);
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004711")
                .queryParam("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("tu-create-invalid"));
    }

    @Test void shall_create_with_error() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4711", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004711")
                .queryParam("strict", "true")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(document("tu-create-error"));
    }

    @Test void shall_update_existing() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("00000000000000004711");
        transportUnit.setActualLocation(new LocationVO(TestData.LOCATION_ID_FGIN0001LEFT));
        mockMvc.perform(put(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004711")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("tu-update",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ));
        TransportUnit tu = service.findByBarcode("00000000000000004711");
        assertThat(tu.getActualLocation().getLocationId()).isEqualTo(LocationPK.fromString(TestData.LOCATION_ID_FGIN0001LEFT));
    }

    @Test void shall_update_404() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("00000000000000004710");
        transportUnit.setActualLocation(new LocationVO(TestData.LOCATION_ID_FGIN0001LEFT));
        mockMvc.perform(put(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004710")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("tu-update-404",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ));
    }

    @Test void shall_move() throws Exception {
        mockMvc.perform(
                patch(API_TRANSPORT_UNITS)
                        .queryParam("bk", "00000000000000004711")
                        .queryParam("newLocation", TestData.LOCATION_ID_FGIN0001LEFT)
        )
                .andExpect(status().isOk())
                .andDo(document("tu-move",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("newLocation").description("The target Location where to move the TransportUnit to")
                        )
                ));
    }

    @Test void shall_add_error() throws Exception {
        mockMvc.perform(post(API_TRANSPORT_UNIT + "/error")
                .queryParam("bk", "00000000000000004711")
                .queryParam("errorCode", "bla"))
                .andExpect(status().isOk())
                .andDo(document("tu-add-error",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit"),
                                parameterWithName("errorCode").description("The error text")
                        )
                ));
    }

    @Test void shall_findByPKey() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS + "/" + TestData.TU_1_PKEY))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openwms.transport-unit-v1+json"))
                .andDo(document("tu-find-by-pkey"));
    }

    @Test void shall_findByBarcode() throws Exception {
        System.setProperty("org.openwms.common.transport.BarcodeFormatProvider", "org.openwms.common.transport.ConfiguredBarcodeFormat");
        System.setProperty("owms.common.barcode.pattern", "");
        System.setProperty("owms.common.barcode.padder", "0");
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", "00000000000000004711"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcode",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ));
    }

    @Test void shall_findByBarcode_short() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", "4711"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcode-short",
                        requestParameters(
                                parameterWithName("bk").description("The identifying Barcode of the TransportUnit")
                        )
                ));
    }

    @Test void shall_findByBarcode_404() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bk", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(CommonMessageCodes.BARCODE_NOT_FOUND)))
                .andDo(document("tu-find-by-barcode-404"));
    }

    @Test void shall_findByBarcodes() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("bks", "00000000000000004711")
                .queryParam("bks", "00000000000000004712")
                .queryParam("bks", "00000000000000004713"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcodes",
                        requestParameters(
                                parameterWithName("bks").description("A set of identifying Barcodes of the TransportUnit to search for")
                                )
                ));
    }

    @Test void shall_findOnLocation() throws Exception {
        mockMvc.perform(get(API_TRANSPORT_UNITS)
                .queryParam("actualLocation", "FGIN/IPNT/0001/0000/0000"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-on-location",
                        requestParameters(
                                parameterWithName("actualLocation").description("The Location to find all TransportUnits booked on")
                                )
                ));
    }
}
