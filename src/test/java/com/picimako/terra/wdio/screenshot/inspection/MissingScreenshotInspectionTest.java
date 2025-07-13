//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraToolkitInspectionTestCase;

/**
 * Unit test for {@link MissingScreenshotInspection}.
 */
public class MissingScreenshotInspectionTest extends TerraToolkitInspectionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new MissingScreenshotInspection();
    }

    public void testMissingScreenshots() {
        copyFilesToProject(
            reference("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"),
            reference("/en/chrome_medium/MissingScreenshots-spec/terra_screenshot[nondefault].png"),
            diff("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"),
            latest("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[nondefault].png"),
            reference("/en/chrome_huge/MissingScreenshots-spec/terra_screenshot[single].png"),
            reference("/en/chrome_huge/MissingScreenshots-spec/testimage[default].png"));

        doWdioSpecTest("tests/wdio/");
    }
}
