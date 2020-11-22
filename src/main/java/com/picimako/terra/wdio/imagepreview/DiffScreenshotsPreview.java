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
