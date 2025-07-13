//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraFunctionalTestingInspectionTestCase;

/**
 * Unit test for {@link DuplicateScreenshotNameInspection}.
 */
public class DuplicateScreenshotNameInspectionTest extends TerraFunctionalTestingInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new DuplicateScreenshotNameInspection();
    }

    public void testDuplicateScreenshotName() {
        copyFileToProject("package.json");
        doWdioSpecTest();
    }

    public void testTriplicateScreenshotName() {
        copyFileToProject("package.json");
        doWdioSpecTest();
    }
}
