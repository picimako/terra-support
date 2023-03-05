//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.viewports.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link DuplicateDescribeViewportsBlockInspection}.
 */
public class DuplicateDescribeViewportsBlockInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/describeviewports/duplicateblocks";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new DuplicateDescribeViewportsBlockInspection();
    }

    public void testNoDescribeViewportsBlock() {
        doWdioSpecTest();
    }

    public void testOneDescribeViewportsBlock() {
        doWdioSpecTest();
    }

    public void testTwoDescribeViewportsBlocksNoReport() {
        doWdioSpecTest();
    }

    public void testTwoDescribeViewportsBlocksReport() {
        doWdioSpecTest();
    }

    public void testMoreThanTwoDescribeViewportsBlocksNoReport() {
        doWdioSpecTest();
    }

    public void testMoreThanTwoDescribeViewportsBlocksConsecutiveReport() {
        doWdioSpecTest();
    }

    public void testMoreThanTwoDescribeViewportsBlocksNonConsecutiveReport() {
        doWdioSpecTest();
    }

    public void testNonArrayDescribeViewportsValues() {
        doWdioSpecTest();
    }
}
