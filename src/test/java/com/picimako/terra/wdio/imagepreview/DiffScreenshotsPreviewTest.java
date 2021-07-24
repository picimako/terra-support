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

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link DiffScreenshotsPreview}.
 */
public class DiffScreenshotsPreviewTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testCollectsScreenshotDiffs() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        VirtualFile vf = myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[2].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));

        myFixture.copyFileToProject(diff("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));

        DiffScreenshotsPreview preview = new DiffScreenshotsPreview(getProject(), vf);
        List<ScreenshotDiff> screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest()).isNull();
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest()).isNull();
    }
}
