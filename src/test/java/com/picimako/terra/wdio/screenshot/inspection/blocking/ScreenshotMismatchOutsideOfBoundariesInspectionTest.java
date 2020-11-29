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

package com.picimako.terra.wdio.screenshot.inspection.blocking;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraInspectionBaseTestCase;
import com.picimako.terra.wdio.screenshot.inspection.ScreenshotMismatchToleranceBlockingInspection;

/**
 * Unit test for {@link ScreenshotMismatchToleranceBlockingInspection}.
 */
public class ScreenshotMismatchOutsideOfBoundariesInspectionTest extends TerraInspectionBaseTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot";
    }

    @Override
    @Nullable
    protected InspectionProfileEntry getInspection() {
        final ScreenshotMismatchToleranceBlockingInspection inspection = new ScreenshotMismatchToleranceBlockingInspection();
        inspection.reportMismatchToleranceIsNonNumeric = false;
        return inspection;
    }

    public void testMismatchToleranceOutsideOfBoundaries() {
        doWdioSpecTest();
    }
}
    