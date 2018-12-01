/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common;

import org.ameba.IDGenerator;
import org.ameba.JdkIDGenerator;
import org.ameba.annotation.EnableAspects;
import org.ameba.http.EnableMultiTenancy;
import org.ameba.http.RequestIDFilter;
import org.ameba.i18n.AbstractTranslator;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.ameba.mapping.DozerMapperImpl;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * A Starter.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
@EnableAspects
@EnableSpringConfigured
@EnableJpaAuditing
@EnableMultiTenancy
@EnableTransactionManagement
@EnableDiscoveryClient
public class ModuleConfiguration {

    public
    @Bean
    Translator translator() {
        return new AbstractTranslator() {
            @Override
            protected MessageSource getMessageSource() {
                return messageSource();
            }
        };
    }

    public
    @Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource nrrbm = new ResourceBundleMessageSource();
        nrrbm.setBasename("i18n");
        return nrrbm;
    }

    public
    @Bean
    BeanMapper beanMapper() {
        return new DozerMapperImpl("classpath:/META-INF/dozer/common-bean-mappings.xml");
    }

    /*~ ------------- Request ID handling ----------- */
    public
    @Bean
    IDGenerator<String> uuidGenerator() {
        return new JdkIDGenerator();
    }

    public
    @Bean
    FilterRegistrationBean requestIDFilter(IDGenerator<String> uuidGenerator) {
        FilterRegistrationBean frb = new FilterRegistrationBean(new RequestIDFilter(uuidGenerator));
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return frb;
    }
}
