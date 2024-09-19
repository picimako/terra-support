//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsInspection}.
 */
public class DuplicateTerraDescribeViewportsInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final TerraDescribeViewportsInspection inspection = new TerraDescribeViewportsInspection();
        inspection.reportViewportsNotInAscendingOrder = false;
        inspection.reportNotSupportedViewports = false;
        inspection.reportEmptyViewports = false;
        inspection.reportNonArrayViewports = false;
        return inspection;
    }

    public void testDuplicateTerraViewports() {
        doWdioSpecTest();
    }

    public void testDuplicateViewportsInDescribeTests() {
        doWdioSpecTestByText(
            """
                Terra.<warning descr="There are duplicate viewport values in this block.">describeTests</warning>('Name', { formFactors: ['small','huge','tiny','huge'] }, () => {
                });

                Terra.<warning descr="There are duplicate viewport values in this block.">describeTests</warning>('Another name', { formFactors: ['small','small','huge','tiny'] }, () => {
                });

                Terra.describeTests('Another name', { formFactors: ['small','huge','tiny'] }, () => {
                });""");
    }
}
