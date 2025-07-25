//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link ScreenshotMismatchToleranceInspection}.
 */
public class ScreenshotMismatchToleranceAboveThresholdInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final ScreenshotMismatchToleranceInspection inspection = new ScreenshotMismatchToleranceInspection();
        inspection.reportMismatchToleranceIsNonNumeric = false;
        inspection.reportMismatchToleranceOutsideOfBoundaries = false;
        return inspection;
    }

    public void testMismatchToleranceAboveThreshold() {
        doWdioSpecTest();
    }
}
    