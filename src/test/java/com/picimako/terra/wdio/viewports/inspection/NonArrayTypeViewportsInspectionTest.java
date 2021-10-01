/*
 * Copyright 2021 Tamás Balog
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
            "Terra.describeTests('Name', { formFactors: <error descr=\"Non-array-type values are not allowed for the viewports argument.\">'asd'</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <error descr=\"Non-array-type values are not allowed for the viewports argument.\">{ }</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <error descr=\"Non-array-type values are not allowed for the viewports argument.\">true</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: <error descr=\"Non-array-type values are not allowed for the viewports argument.\">45</error> }, () => {\n" +
                "});\n" +
                "\n" +
                "Terra.describeTests('Another name', { formFactors: ['huge'] }, () => {\n" +
                "});");
    }
}
