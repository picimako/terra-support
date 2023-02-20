//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
