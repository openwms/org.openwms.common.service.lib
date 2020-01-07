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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.CommonApplicationTest;
import org.openwms.common.CommonConstants;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.TestData;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.transport.api.TransportUnitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation, WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get(CommonConstants.API_TRANSPORT_UNITS + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcode").exists())
                .andExpect(jsonPath("$._links.transport-unit-findbybarcodes").exists())
                .andExpect(jsonPath("$._links.transport-unit-findonlocation").exists())
                .andExpect(jsonPath("$._links.length()", is(3)))
                .andDo(document("tu-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_createSimple() throws Exception {
        mockMvc.perform(post(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004710")
                .param("actualLocation", TestData.LOCATION_ID_EXT)
                .param("tut", TestData.TUT_TYPE_PALLET)
                .param("strict", "false"))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tu-create-simple"));
    }

    @Test void shall_createSimple_with_error() throws Exception {
        mockMvc.perform(post(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004711")
                .param("actualLocation", TestData.LOCATION_ID_EXT)
                .param("tut", TestData.TUT_TYPE_PALLET)
                .param("strict", "true"))
                .andExpect(status().isConflict())
                .andDo(document("tu-create-simple-error"));
    }

    @Test void shall_createFull() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4710", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004710")
                .param("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tu-create-full"));
    }

    @Test void shall_create_with_invalid() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4711", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        transportUnit.setActualLocation(null);
        mockMvc.perform(post(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004711")
                .param("strict", "false")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("tu-create-invalid"));
    }

    @Test void shall_create_with_error() throws Exception {
        TransportUnitVO transportUnit = new TransportUnitVO("4711", TestData.TUT_TYPE_PALLET, new LocationVO(TestData.LOCATION_ID_EXT));
        mockMvc.perform(post(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004711")
                .param("strict", "true")
                .content(om.writeValueAsString(transportUnit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(document("tu-create-error"));
    }

    @Test void shall_findByBarcode() throws Exception {
        mockMvc.perform(get(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "00000000000000004711"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcode"));
    }

    @Test void shall_findByBarcode_short() throws Exception {
        mockMvc.perform(get(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "4711"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcode-short"));
    }

    @Test void shall_findByBarcode_404() throws Exception {
        mockMvc.perform(get(CommonConstants.API_TRANSPORT_UNITS)
                .param("bk", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(CommonMessageCodes.BARCODE_NOT_FOUND)))
                .andDo(document("tu-find-by-barcode-404"));
    }

    @Test void shall_findByBarcodes() throws Exception {
        mockMvc.perform(get(CommonConstants.API_TRANSPORT_UNITS)
                .param("bks", "00000000000000004711")
                .param("bks", "00000000000000004712")
                .param("bks", "00000000000000004713"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-by-barcodes"));
    }

    @Test void shall_findOnLocation() throws Exception {
        mockMvc.perform(get(CommonConstants.API_TRANSPORT_UNITS)
                .param("actualLocation", "FGIN/IPNT/0001/0000/0000"))
                .andExpect(status().isOk())
                .andDo(document("tu-find-on-location"));
    }
}
