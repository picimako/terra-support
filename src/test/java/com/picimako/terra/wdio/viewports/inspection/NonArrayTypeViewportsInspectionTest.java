//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsInspection}.
 */
public class NonArrayTypeViewportsInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final TerraDescribeViewportsInspection inspection = new TerraDescribeViewportsInspection();
        inspection.reportEmptyViewports = false;
        inspection.reportNotSupportedViewports = false;
        inspection.reportDuplicateViewports = false;
        inspection.reportViewportsNotInAscendingOrder = false;
        return inspection;
    }

    public void testNonArrayTypeViewports() {
        doWdioSpecTest();
    }

    public void testNonArrayTypeViewportsInDescribeTests() {
        doWdioSpecTestByText(
            """
                Terra.describeTests('Name', { formFactors: <error descr="Non-array-type values are not allowed for the viewports argument.">'asd'</error> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: <error descr="Non-array-type values are not allowed for the viewports argument.">{ }</error> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: <error descr="Non-array-type values are not allowed for the viewports argument.">true</error> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: <error descr="Non-array-type values are not allowed for the viewports argument.">45</error> }, () => {
                });

                Terra.describeTests('Another name', { formFactors: ['huge'] }, () => {
                });""");
    }
}
