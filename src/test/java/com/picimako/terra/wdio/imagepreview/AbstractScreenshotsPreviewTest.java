//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.ScreenshotTypeHelper.diff;
import static com.picimako.terra.wdio.ScreenshotTypeHelper.reference;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link AbstractScreenshotsPreview}.
 */
public class AbstractScreenshotsPreviewTest extends TerraToolkitTestCase {

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

        DummyScreenshotPreview preview = new DummyScreenshotPreview(getProject(), vf, "diff", ScreenshotDiff::new);
        List<ScreenshotDiff> screenshotDiffs = preview.getScreenshotDiffs();

        assertThat(screenshotDiffs).hasSize(2);
        assertThat(screenshotDiffs.get(0).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_huge/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(0).getLatest()).isNull();
        assertThat(screenshotDiffs.get(1).getOriginal().getPath()).isEqualTo("/src/tests/wdio/__snapshots__/diff/en/chrome_medium/ScreenshotPreview-spec/screenshot_preview[1].png");
        assertThat(screenshotDiffs.get(1).getLatest()).isNull();
    }

    static class DummyScreenshotPreview extends AbstractScreenshotsPreview {

        protected DummyScreenshotPreview(@NotNull Project project, @NotNull VirtualFile file, @NotNull String sourceFolderName, @NotNull Function<VirtualFile, ScreenshotDiff> screenshotToDiffMapper) {
            super(project, file, sourceFolderName, screenshotToDiffMapper);
        }

        @Override
        protected @NotNull ScreenshotDiffUIContentProvider uiContentProvider() {
            return new DiffScreenshotsUIProvider(project);
        }

        @Override
        public @NotNull String getName() {
            return "Dummy Preview";
        }
    }
}
