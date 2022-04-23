//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraFunctionalTestingInspectionTestCase;
import com.picimako.terra.wdio.inspection.ReplaceTerraItWithTerraValidatesInspection;

/**
 * Unit test for {@link ReplaceTerraItWithTerraValidatesInspection}.
 */
public class ReplaceTerraItWithTerraValidatesInspectionForScreenshotTest extends TerraFunctionalTestingInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot/replaceit/screenshot";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new ReplaceTerraItWithTerraValidatesInspection();
    }

    public void testItValidatesScreenshotToValidatesScreenshot() {
        doQuickFixTest("ItValidatesScreenshot", TerraBundle.inspection("replace.terra.it.simple"));
    }

    public void testItValidatesScreenshotWithName() {
        doQuickFixTest("ItValidatesScreenshotWithName", TerraBundle.inspection("replace.terra.it.simple"));
    }

    public void testItValidatesScreenshotWithArguments() {
        doQuickFixTest("ItValidatesScreenshotWithArguments", TerraBundle.inspection("replace.terra.it.simple"));
    }
}
