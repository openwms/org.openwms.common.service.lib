/*
 * Copyright 2005-2019 the original author or authors.
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

import org.ameba.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.ApplicationTest;
import org.openwms.common.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TargetControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@ApplicationTest
class TargetControllerDocumentation {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_lock_LocationGroup() throws Exception {
        mockMvc.perform(post(CommonConstants.API_TARGETS + "/IPOINT")
                        .param("reallocation", "true")
                        .param("op", "lock"))
                .andExpect(status().isOk())
                .andDo(document("lock-lg-IPOINT"));
    }

    @Test void shall_lock_unknown_LocationGroup() throws Exception {
        mockMvc.perform(post(CommonConstants.API_TARGETS + "/FOO")
                .param("op", "lock"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("messageKey", is(Messages.NOT_FOUND)))
                .andExpect(jsonPath("httpStatus", is("404")))
                .andDo(document("lock-lg-unknown"));
    }

    @Test void shall_unlock_LocationGroup() throws Exception {
        mockMvc.perform(post(CommonConstants.API_TARGETS + "/IPOINT")
                .param("op", "unlock"))
                .andExpect(status().isOk())
                .andDo(document("unlock-lg-IPOINT"));
    }
}
