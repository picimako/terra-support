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

import java.awt.*;

import com.intellij.openapi.project.Project;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Provides a Swing component for displaying a single Terra wdio diff image (as in from a diff folder).
 * <p>
 * The returned component is an {@link ImageEditorImpl} displaying its original toolbar as well.
 *
 * @see DiffScreenshotsPreview
 */
public final class DiffScreenshotsUIProvider extends AbstractScreenshotDiffUIContentProvider {

    public DiffScreenshotsUIProvider(@NotNull Project project) {
        super(project);
    }

    @Override
    public Component getContent(@NotNull ScreenshotDiff screenshotDiff) {
        return createImageEditorFor(screenshotDiff.getOriginal());
    }
}
