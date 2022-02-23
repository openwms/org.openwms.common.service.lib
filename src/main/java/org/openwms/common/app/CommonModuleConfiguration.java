/*
 * Copyright 2005-2022 the original author or authors.
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

import io.micrometer.core.instrument.MeterRegistry;
import org.ameba.IDGenerator;
import org.ameba.JdkIDGenerator;
import org.ameba.annotation.EnableAspects;
import org.ameba.app.BaseClientHttpRequestInterceptor;
import org.ameba.app.SpringProfiles;
import org.ameba.http.EnableMultiTenancy;
import org.ameba.http.PermitAllCorsConfigurationSource;
import org.ameba.http.RequestIDFilter;
import org.ameba.http.identity.EnableIdentityAwareness;
import org.ameba.i18n.AbstractSpringTranslator;
import org.ameba.i18n.Translator;
import org.ameba.system.NestedReloadableResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.Ordered;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.Filter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * A CommonModuleConfiguration.
 *
 * @author Heiko Scherrer
 */
@Configuration
@RefreshScope
@EnableAspects(propagateRootCause = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties
@EnableIdentityAwareness
@EnableSpringConfigured
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "org.openwms", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EntityScan(basePackages = "org.openwms")
@EnableMultiTenancy(enabled = false)
@EnableTransactionManagement
public class CommonModuleConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @RefreshScope
    @Bean MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${spring.application.name}") String applicationName) {
        return registry -> registry.config().commonTags("application", applicationName);
    }

    @LoadBalanced
    @Bean
    RestTemplate aLoadBalanced(List<BaseClientHttpRequestInterceptor> baseInterceptors) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().addAll(baseInterceptors);
        return restTemplate;
    }

    public @Bean LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    public @Bean LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    public @Bean Translator translator() {
        return new AbstractSpringTranslator() {
            @Override
            protected MessageSource getMessageSource() {
                return messageSource();
            }
        };
    }

    public @Bean MessageSource messageSource() {
        NestedReloadableResourceBundleMessageSource nrrbm = new NestedReloadableResourceBundleMessageSource();
        nrrbm.setBasenames(
                "classpath:META-INF/i18n/common",
                "classpath:META-INF/i18n/common-val"
        );
        nrrbm.setDefaultEncoding("UTF-8");
        nrrbm.setCommonMessages(new Properties());
        return nrrbm;
    }

    @Profile(SpringProfiles.DEVELOPMENT_PROFILE)
    public @Bean Filter corsFiler() {
        return new CorsFilter(new PermitAllCorsConfigurationSource());
    }

    /*~ ------------- Request ID handling ----------- */
    public @Bean IDGenerator<String> uuidGenerator() {
        return new JdkIDGenerator();
    }

    public @Bean FilterRegistrationBean<RequestIDFilter> requestIDFilter(IDGenerator<String> uuidGenerator) {
        var frb = new FilterRegistrationBean<>(new RequestIDFilter(uuidGenerator));
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return frb;
    }
}
