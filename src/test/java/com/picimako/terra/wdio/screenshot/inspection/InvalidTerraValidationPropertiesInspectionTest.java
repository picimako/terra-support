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

package com.picimako.terra.wdio.screenshot.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraInspectionBaseTestCase;

/**
 * Unit test for {@link InvalidTerraValidationPropertiesInspection}.
 */
public class InvalidTerraValidationPropertiesInspectionTest extends TerraInspectionBaseTestCase {

    @Override
    protected String getTestDataPath() {
        return BASE_PATH + "/wdio/screenshot";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new InvalidTerraValidationPropertiesInspection();
    }

    public void testInvalidTerraValidationProperties() {
        doWdioSpecTest();
    }

    public void testInvalidTerraValidationPropertiesTerraFunctionalTesting() {
        myFixture.copyFileToProject("package.json");
        doWdioSpecTest();
    }

    //To fix the test failure when copying package.json to the project
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }
}
