//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import com.picimako.terra.TerraToolkitTestCase;
import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Unit test for {@link ReferenceToLatestScreenshotsPreview}.
 */
public class ReferenceToLatestScreenshotsPreviewTest extends TerraToolkitTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    public void testCollectsScreenshotDiffsWithoutLatest() {
        copyFilesToProject(
            reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"),
            reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        var vf = copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));
        copyFilesToProject(
            reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[2].png"),
            diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"),
            diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"),
            diff("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));

        TerraWdioFolders.setWdioTestRootPath("tests/wdio");

        var preview = new ReferenceToLatestScreenshotsPreview(getProject(), vf);
        var screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest()).isNull();
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest()).isNull();
    }

    public void testCollectsScreenshotDiffsWithLatest() {
        copyFilesToProject(
            reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"),
            reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        var vf = copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));
        copyFilesToProject(
            reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[2].png"),
            latest("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"),
            latest("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));

        TerraWdioFolders.setWdioTestRootPath("tests/wdio");

        var preview = new ReferenceToLatestScreenshotsPreview(getProject(), vf);
        var screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
    }
}
