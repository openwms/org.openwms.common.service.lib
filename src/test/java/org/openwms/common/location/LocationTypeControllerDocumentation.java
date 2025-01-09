/*
 * Copyright 2005-2025 the original author or authors.
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
import org.openwms.common.location.api.LocationApiConstants;
import org.openwms.common.spi.transactions.commands.AsyncTransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A LocationTypeControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@SuppressWarnings("squid:S3577")
@CommonApplicationTest
class LocationTypeControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LocationService service;
    @MockitoBean
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
                        get(LocationApiConstants.API_LOCATION_TYPES + "/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.length()", is(3)))
                .andExpect(jsonPath("$._links.location-types-findbypkey").exists())
                .andExpect(jsonPath("$._links.location-types-findbytypename").exists())
                .andExpect(jsonPath("$._links.location-types-findall").exists())
                .andDo(document("loctype-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_find_by_pKey() throws Exception {
        mockMvc.perform(get(LocationApiConstants.API_LOCATION_TYPES + "/326981811784"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("FG")))
                .andDo(document("loctype-findbypkey",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("_links").description("An array with hyperlinks to corresponding resources"),
                                fieldWithPath("_links.location-types-findbypkey").description("A link to get the resource by persistent key"),
                                fieldWithPath("_links.location-types-findbypkey.*").ignored(),
                                fieldWithPath("pKey").description("The persistent technical key of the LocationType"),
                                fieldWithPath("type").description("Unique natural key"),
                                fieldWithPath("description").description("A descriptive text of the LocationType"),
                                fieldWithPath("length").description("The typical length of a Location belonging to this type"),
                                fieldWithPath("width").description("The typical width of a Location belonging to this type"),
                                fieldWithPath("height").description("The typical height of a Location belonging to this type"),
                                fieldWithPath("createDt").description("Timestamp when the LocationType has been created")
                        )
                ))
                ;
    }

    @Test void shall_find_by_pKey_404() throws Exception {
        mockMvc.perform(get(LocationApiConstants.API_LOCATION_TYPES + "/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andDo(document("loctype-findbypkey-404",
                        preprocessResponse(prettyPrint())
                ))
        ;
    }

    @Test void shall_find_by_name() throws Exception {
        mockMvc.perform(get(LocationApiConstants.API_LOCATION_TYPES)
                        .queryParam("typeName", "FG")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("FG")))
                .andDo(document("loctype-findbytypename",
                        preprocessResponse(prettyPrint())
                ))
        ;
    }

    @Test void shall_find_by_name_404() throws Exception {
        mockMvc.perform(get(LocationApiConstants.API_LOCATION_TYPES)
                        .queryParam("typeName", "UNKNOWN")
                )
                .andExpect(status().isNotFound())
                .andDo(document("loctype-findbytypename-404",
                        preprocessResponse(prettyPrint())
                ))
        ;
    }

    @Test void shall_findall() throws Exception {
        mockMvc.perform(get(LocationApiConstants.API_LOCATION_TYPES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andDo(document("loctype-findall",
                        preprocessResponse(prettyPrint()))
                );
    }
}

