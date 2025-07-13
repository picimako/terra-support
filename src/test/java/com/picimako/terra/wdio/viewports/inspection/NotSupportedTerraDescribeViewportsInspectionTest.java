//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsInspection}.
 */
public class NotSupportedTerraDescribeViewportsInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final TerraDescribeViewportsInspection inspection = new TerraDescribeViewportsInspection();
        inspection.reportEmptyViewports = false;
        inspection.reportNonArrayViewports = false;
        inspection.reportDuplicateViewports = false;
        inspection.reportViewportsNotInAscendingOrder = false;
        return inspection;
    }

    public void testNotSupportedTerraViewports() {
        doWdioSpecTest();
    }

    public void testNotSupportedViewportsInDescribeTests() {
        doWdioSpecTestByText(
            """
                Terra.describeTests('Name', { formFactors: [<error descr="This viewport is not supported by Terra.">''</error>,'huge','tiny'] }, () => {
                });

                Terra.describeTests('Another name', { formFactors: ['small',<error descr="This viewport is not supported by Terra.">'gigantic'</error>,'tiny'] }, () => {
                });

                Terra.describeTests('Another name', { formFactors: ['small','huge','tiny'] }, () => {
                });""");
    }
}
