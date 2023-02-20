//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.latest;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;

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
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        VirtualFile vf = myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[2].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(diff("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        myFixture.copyFileToProject(diff("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));

        TerraWdioFolders.setWdioTestRootPath("tests/wdio");

        ReferenceToLatestScreenshotsPreview preview = new ReferenceToLatestScreenshotsPreview(getProject(), vf);
        List<ScreenshotDiff> screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest()).isNull();
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest()).isNull();
    }

    public void testCollectsScreenshotDiffsWithLatest() {
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[2].png"));
        VirtualFile vf = myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(reference("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[2].png"));
        myFixture.copyFileToProject(latest("/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png"));
        myFixture.copyFileToProject(latest("/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png"));

        TerraWdioFolders.setWdioTestRootPath("tests/wdio");

        ReferenceToLatestScreenshotsPreview preview = new ReferenceToLatestScreenshotsPreview(getProject(), vf);
        List<ScreenshotDiff> screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/reference/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/latest/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
    }
}
