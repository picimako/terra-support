//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.TerraWdioFolders.DIFF;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * A custom {@link com.intellij.openapi.fileEditor.FileEditor} implementation that accumulates Terra {@code diff} images
 * for all locales, browsers and viewports for a given filename into a single editor / view.
 * <p>
 * The filename to gather the files for is determined by the name of the file that triggers the opening of an editor, for
 * example double-clicking on a diff image file in the project view, or selecting the {@code Show Diffs} context menu option
 * in the Terra wdio tool window.
 *
 * @see DiffScreenshotsUIProvider
 * @since 0.1.0
 */
public final class DiffScreenshotsPreview extends AbstractScreenshotsPreview {
    public static final String EDITOR_TYPE_ID = "Terra.Diff.Image.Preview";

    public DiffScreenshotsPreview(@NotNull Project project, @NotNull VirtualFile file) {
        super(project, file, DIFF, ScreenshotDiff::new);
    }

    @Override
    public @NotNull String getName() {
        return "Terra: Diff Preview";
    }

    @Override
    protected @NotNull ScreenshotDiffUIContentProvider uiContentProvider() {
        return new DiffScreenshotsUIProvider(project);
    }
}
