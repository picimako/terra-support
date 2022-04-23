//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
