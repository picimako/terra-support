//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import java.awt.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a Swing UI component for displaying screenshot diffs.
 *
 * @see DiffScreenshotsUIProvider
 * @see ReferenceToLatestScreenshotsUIProvider
 */
public interface ScreenshotDiffUIContentProvider {

    /**
     * Assembles a Swing component, preferably an {@link org.intellij.images.editor.impl.ImageEditorImpl} for displaying
     * the screenshots provided by the argument object.
     *
     * @param screenshotDiff stores the original and latest screenshots for diffing
     * @return the Swing component that displays the screenshot diff
     */
    @Nullable
    Component getContent(@NotNull ScreenshotDiff screenshotDiff);
}
