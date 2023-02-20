//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.helpers.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;
import com.picimako.terra.wdio.helpers.inspection.NestedTerraDescribeHelpersNotAllowedInspection;

/**
 * Unit test for {@link NestedTerraDescribeHelpersNotAllowedInspection}.
 */
public class NestedTerraDescribeHelpersNotAllowedInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        return new NestedTerraDescribeHelpersNotAllowedInspection();
    }

    public void testNestedTerraDescribeHelpers() {
        doWdioSpecTest();
    }
}
    