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

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * A EnsureArchitectureTest.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class,
        ImportOption.DoNotIncludeArchives.class
})
class EnsureArchitectureTest {

    @ArchTest
    public static final ArchRule verify_account_api_package =
            classes().that()
                    .resideInAPackage("..account.api..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage("..account.api..",
                            "org.ameba..",
                            "org.openwms.core..",
                            "java..", "javax..", "jakarta..",
                            "org.springframework..",
                            "com..")
                    .because("The API package is separated and the only package accessible by the client")
            ;

    @ArchTest
    public static final ArchRule verify_api_package =
            classes().that()
                    .resideInAPackage("..location.api..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage("..location.api..",
                            "org.ameba..",
                            "org.openwms.core..",
                            "java..", "javax..", "jakarta..",
                            "org.springframework..",
                            "com.."
                    )
                    .because("The API package is separated and the only package accessible by the client")
            ;

    @ArchTest
    public static final ArchRule verify_no_direct_impl_access =
            noClasses().that()
                    .resideInAPackage("..location")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..location.impl..", "..location.spi..");

    @ArchTest
    public static final ArchRule verify_spi_access =
            classes().that()
                    .resideInAPackage("..location.spi")
                    .should()
                    .onlyHaveDependentClassesThat()
                    .resideInAnyPackage("..location.impl", "..location.spi", "java..");

    @ArchTest
    public static final ArchRule verify_no_cycles_location =
            slices().matching("..(location)..").should().beFreeOfCycles();

    @ArchTest
    public static final ArchRule verify_no_cycles_transport =
            slices().matching("..(transport)..").should().beFreeOfCycles();

    @ArchIgnore(reason = "..(*). is wrong and must be ..(*).. this will break in the next ArchUnit release that's more strict 0.23.0")
    @ArchTest
    public static final ArchRule verify_no_cycles =
            slices().matching("..(*).").should().beFreeOfCycles();
}
