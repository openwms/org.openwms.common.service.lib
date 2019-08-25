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
package org.openwms.common;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * A EnsureArchitectureIT.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms.common", importOptions = {ImportOption.DoNotIncludeTests.class})
class EnsureArchitectureIT {

    @ArchTest
    public static final ArchRule rule1 =
            noClasses().that()
                    .resideInAPackage("..location")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..location.impl..", "..location.spi..");

    @ArchTest
    public static final ArchRule rule2 =
            classes().that()
                    .resideInAPackage("..location.spi")
                    .should()
                    .onlyHaveDependentClassesThat()
                    .resideInAnyPackage("..location.impl", "..location.spi", "java..");

    @ArchTest
    public static final ArchRule verify_api_package =
            classes().that()
                    .resideInAPackage("..location.api..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage("..location.api..", "org.openwms.core..", "java..", "org.springframework..")
            ;

    @ArchTest
    public static final ArchRule cycles =
            slices().matching("org.openwms.(*)..").should().beFreeOfCycles();
}
