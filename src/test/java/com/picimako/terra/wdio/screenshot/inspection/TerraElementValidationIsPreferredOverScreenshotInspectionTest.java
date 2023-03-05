//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraElementValidationIsPreferredOverScreenshotInspection}.
 */
public class TerraElementValidationIsPreferredOverScreenshotInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        return new TerraElementValidationIsPreferredOverScreenshotInspection();
    }

    public void testElementOverScreenshot() {
        doWdioSpecTest();
    }
}
    