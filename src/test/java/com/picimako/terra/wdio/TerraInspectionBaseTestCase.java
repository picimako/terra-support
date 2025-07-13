//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.openapi.vfs.VirtualFile;
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
    protected void doWdioSpecTest(String path) {
        doCustomFileTest(path, ".js", null, "-spec");
    }

    protected void doWdioSpecTest() {
        doCustomFileTest("", ".js", null, "-spec");
    }

    protected void doWdioSpecTestByText(String text) {
        myFixture.configureByText("Wdio-spec.js", text);
        myFixture.enableInspections(getInspection());
        myFixture.testHighlighting(true, false, false);
    }

    protected void doQuickFixTest(String filename, String quickFixName) {
        myFixture.configureByFile(filename + "-spec.js");
        myFixture.enableInspections(getInspection());
        myFixture.doHighlighting();
        myFixture.launchAction(myFixture.findSingleIntention(quickFixName));
        myFixture.checkResultByFile(filename + "-spec.after.js");
    }

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
    private void doCustomFileTest(String path, @NotNull String extension, @Nullable InspectionProfileEntry inspection, @NotNull String... postfixes) {
        final String testDataFileName = postfixes.length == 1
            ? path + getTestName(false) + postfixes[0] + extension
            : path + getTestName(false) + extension;
        myFixture.enableInspections(inspection == null ? getInspection() : inspection);
        myFixture.configureByFile(testDataFileName);
        myFixture.testHighlighting(true, false, false);
    }

    protected VirtualFile copyFileToProject(String sourceFilePath) {
        return myFixture.copyFileToProject(sourceFilePath);
    }

    protected void copyFilesToProject(String... sourceFilePaths) {
        for (String path : sourceFilePaths) {
            myFixture.copyFileToProject(path);
        }
    }
}
