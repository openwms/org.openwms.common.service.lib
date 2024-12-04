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
package org.openwms.common.app;

import org.ameba.http.PermitAllCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

/**
 * A CommonSecurityConfiguration.
 *
 * @author Heiko Scherrer
 */
@Configuration
class CommonSecurityConfiguration {

    /**
     * {@inheritDoc}
     * <p>
     * API is for non browser clients and access control is handled at the API Gateway!
     */
    @SuppressWarnings("java:S4502")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(x -> x.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .addFilter(new CorsFilter(new PermitAllCorsConfigurationSource()));
        return http.build();
    }
}
