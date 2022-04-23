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
public class ReplaceTerraItWithTerraValidatesInspectionForAccessibilityTest extends TerraFunctionalTestingInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot/replaceit/accessibility";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new ReplaceTerraItWithTerraValidatesInspection();
    }

    public void testItValidatesAccessibilityToValidatesAccessibility() {
        doQuickFixTest("ItValidatesAccessibility", TerraBundle.inspection("replace.terra.it.simple"));
    }

    public void testItValidatesAccessibilityWithArguments() {
        doQuickFixTest("ItValidatesAccessibilityWithArguments", TerraBundle.inspection("replace.terra.it.simple"));
    }
}
