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

package com.picimako.terra.wdio.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraFunctionalTestingInspectionTestCase;
import com.picimako.terra.wdio.inspection.ReplaceTerraItWithTerraValidatesInspection;

/**
 * Unit test for {@link ReplaceTerraItWithTerraValidatesInspection}.
 */
public class ReplaceTerraItWithTerraValidatesInspectionForAccessibilityTest extends TerraFunctionalTestingInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot/replaceit/accessibility";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new ReplaceTerraItWithTerraValidatesInspection();
    }

    public void testItValidatesAccessibilityToValidatesAccessibility() {
        doQuickFixTest("ItValidatesAccessibility", TerraBundle.inspection("replace.terra.it.simple"));
    }

    public void testItValidatesAccessibilityWithArguments() {
        doQuickFixTest("ItValidatesAccessibilityWithArguments", TerraBundle.inspection("replace.terra.it.simple"));
    }
}
