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

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraInspectionBaseTestCase;

/**
 * Unit test for {@link MissingScreenshotInspection}.
 */
public class MissingScreenshotInspectionTest extends TerraInspectionBaseTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new MissingScreenshotInspection();
    }

    public void testMissingScreenshots() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/MissingScreenshots-spec/terra_screenshot[nondefault].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[single].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/MissingScreenshots-spec/testimage[default].png"));
        doWdioSpecTest("tests/wdio/");
    }
}
