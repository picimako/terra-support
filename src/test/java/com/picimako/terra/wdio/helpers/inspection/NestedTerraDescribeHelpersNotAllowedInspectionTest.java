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
    