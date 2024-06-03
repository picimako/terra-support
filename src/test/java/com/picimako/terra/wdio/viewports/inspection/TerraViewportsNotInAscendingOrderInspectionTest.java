//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsInspection}.
 */
public class TerraViewportsNotInAscendingOrderInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final TerraDescribeViewportsInspection inspection = new TerraDescribeViewportsInspection();
        inspection.reportDuplicateViewports = false;
        inspection.reportEmptyViewports = false;
        inspection.reportNonArrayViewports = false;
        inspection.reportNotSupportedViewports = false;
        return inspection;
    }

    public void testTerraViewportsNotInAscendingOrder() {
        doWdioSpecTest();
    }

    public void testTerraViewportsNotInAscendingOrderInDescribeTests() {
        doWdioSpecTestByText(
            """
                Terra.describeTests('Name', { formFactors: ['huge','enormous'] }, () => {
                });

                Terra.describeTests('Another name', { formFactors: <warning descr="Viewports are not in ascending order by their widths.">['small','medium','tiny']</warning> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: <warning descr="Viewports are not in ascending order by their widths.">['huge','small','tiny']</warning> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: ['huge'] }, () => {
                });""");
    }
}
