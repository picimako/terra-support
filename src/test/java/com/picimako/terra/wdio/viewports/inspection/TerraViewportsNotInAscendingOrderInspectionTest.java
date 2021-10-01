/*
 * Copyright 2020 Tamás Balog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            "Terra.describeTests('Name', { formFactors: ['huge','enormous'] }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <warning descr=\"Viewports are not in ascending order by their widths.\">['small','medium','tiny']</warning> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <warning descr=\"Viewports are not in ascending order by their widths.\">['huge','small','tiny']</warning> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: ['huge'] }, () => {\n" +
                "});");
    }
}
