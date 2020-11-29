/*
 * Copyright 2020 Tamás Balog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.picimako.terra.wdio;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base unit test class for validating Terra specific inspections in JavaScript and related files.
 * <p>
 * Most methods in this class are based on {@code com.siyeh.ig.LightJavaInspectionTestCase#doTest()}.
 */
public abstract class TerraInspectionBaseTestCase extends BasePlatformTestCase {

    /**
     * Base folder path for locating the test data files.
     * <p>
     * To specify the exact folder path in a specific inspection unit test class, override the {@link #getTestDataPath()}
     * method:
     * <pre>
     * &#064;Override
     * protected String getTestDataPath() {
     *     return BASE_PATH + "/wdio/describeviewports";
     * }
     * </pre>
     */
    protected static final String BASE_PATH = "testdata/terra/inspection";

    @Nullable
    protected abstract InspectionProfileEntry getInspection();

    /**
     * Configures the underlying virtual file system with the current test wdio {@code -spec.js} postfixed file
     * and validates the highlighting.
     * <p>
     * When this method is called in a unit test method, the unit test method's name must not include the {@code -spec}
     * postfix, otherwise the platform won't find it.
     */
    protected void doWdioSpecTest() {
        doCustomFileTest(".js", null, "-spec");
    }

//    /**
//     * Configures the underlying virtual file system with the current test .js file and validates the highlighting.
//     * <p>
//     * This method is based on {@link LightJavaInspectionTestCase#doTest()}.
//     */
//    protected void doJsTest() {
//        doCustomFileTest(".js");
//    }

//    protected void doJsTest(@NotNull InspectionProfileEntry inspection) {
//        doCustomFileTest(".js", inspection);
//    }

    /**
     * Loads a test data file into the virtual file system, corresponding to the unit test method's name, then executes
     * it and validates highlighting.
     * <p>
     * The {@code postfixes} argument is meant to contain either 1 or 0 values, making it a vararg was purely to have
     * cleaner code when calling the method with 0 arguments.
     * <p>
     * If there is any postfix, it will be built into the desired filename, otherwise only the method's name will be
     * taken into account.
     *
     * @param extension the file extension to use for retrieving the test data file
     * @param postfixes any file name postfix to take into account during test data retrieval
     */
    private void doCustomFileTest(@NotNull String extension, @Nullable InspectionProfileEntry inspection, @NotNull String... postfixes) {
        final String testDataFileName = postfixes.length == 1
            ? getTestName(false) + postfixes[0] + extension
            : getTestName(false) + extension;
        myFixture.enableInspections(inspection == null ? getInspection() : inspection);
        myFixture.configureByFile(testDataFileName);
        myFixture.testHighlighting(true, false, false);
    }
}
