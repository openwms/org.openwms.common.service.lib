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
package org.openwms.common;

import org.ameba.annotation.EnableAspects;
import org.ameba.test.categories.SpringTestSupport;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A CommonDataTest.
 *
 * @author Heiko Scherrer
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringTestSupport
@SqlGroup({
        @Sql(scripts = "classpath:test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@DataJpaTest(showSql = false, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.jpa.show-sql=false",
        "spring.main.banner-mode=OFF",
        "spring.jackson.serialization.INDENT_OUTPUT=true"
})
@EntityScan(basePackageClasses = CommonConstants.class)
@EnableAspects(propagateRootCause = true)
@EnableConfigurationProperties
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"org.openwms.common*", "org.openwms.common.transport.barcode"}, repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableSpringConfigured
public @interface CommonDataTest {
}
