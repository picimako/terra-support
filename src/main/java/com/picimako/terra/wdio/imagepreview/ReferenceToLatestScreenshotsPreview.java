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

import static com.picimako.terra.wdio.TerraWdioFolders.REFERENCE;
import static com.picimako.terra.wdio.TerraWdioFolders.latestImageForReference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * A custom {@link com.intellij.openapi.fileEditor.FileEditor} implementation that accumulates Terra {@code reference}
 * and {@code latest} images for all locales, browsers and viewports for a given filename into a single editor / view.
 * <p>
 * Then each pair of reference and latest images are display in a side-by-side split view to make their comparison easier.
 * <p>
 * Above each split view, there is the respective context string (locale | browser | viewport) displayed.
 * <p>
 * The filename to gather the files for is determined by the name of the file that triggers the opening of an editor, for
 * example double-clicking on a reference or latest image file in the project view, or selecting the
 * {@code Compare Latests With References} context menu option in the Terra wdio tool window.
 * <p>
 * If, for some reason no image can be display, a message saying {@code There is no screenshot available to display.}
 * is shown instead.
 *
 * @see ReferenceToLatestScreenshotsUIProvider
 * @since 0.1.0
 */
public class ReferenceToLatestScreenshotsPreview extends AbstractScreenshotsPreview {

    public static final String EDITOR_TYPE_ID = "Terra.Reference.To.Latest.Image.Preview";

    public ReferenceToLatestScreenshotsPreview(@NotNull Project project, @NotNull VirtualFile file) {
        super(project, file, REFERENCE, screenshot -> new ScreenshotDiff(screenshot, latestImageForReference(project, screenshot)));
    }

    @Override
    public @NotNull String getName() {
        return "Terra: Reference / Latest Preview";
    }

    @Override
    protected @NotNull ScreenshotDiffUIContentProvider uiContentProvider() {
        return new ReferenceToLatestScreenshotsUIProvider(project);
    }
}
