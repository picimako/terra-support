//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsInspection}.
 */
public class NoActualTerraViewportsInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final TerraDescribeViewportsInspection inspection = new TerraDescribeViewportsInspection();
        inspection.reportNotSupportedViewports = false;
        inspection.reportNonArrayViewports = false;
        inspection.reportDuplicateViewports = false;
        inspection.reportViewportsNotInAscendingOrder = false;
        return inspection;
    }

    public void testNoActualTerraViewportDefined() {
        doWdioSpecTest();
    }

    public void testNoActualTerraViewportDefinedInDescribeTests() {
        doWdioSpecTestByText(
            "Terra.describeTests('Name', { formFactors: <error descr=\"There is no actual viewport specified.\">['']</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <error descr=\"There is no actual viewport specified.\">['', '']</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <error descr=\"There is no actual viewport specified.\">[]</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: ['huge'] }, () => {\n" +
                "});");
    }
}
