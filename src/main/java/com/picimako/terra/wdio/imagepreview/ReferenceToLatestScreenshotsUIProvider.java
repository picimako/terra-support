//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import java.awt.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Provides a Swing component for displaying Terra wdio reference/latest images.
 * <p>
 * The returned component is an {@link ImageEditorImpl} displaying its original toolbar as well.
 * <p>
 * If there are both a reference and a latest image present in the provided {@link ScreenshotDiff}, then each of them
 * are are displayed in a reference/latest split view, which can also be resized by sliding the middle separator to the
 * left/right, but its position is not saved.
 * <p>
 * If, for some reason, there is no latest image provided (meaning that it was probably removed manually), then this
 * screenshot diff will not be included in the related preview, since showing only the reference image doesn't seem to
 * be helpful in test failure investigation.
 *
 * @see ReferenceToLatestScreenshotsPreview
 */
public final class ReferenceToLatestScreenshotsUIProvider extends AbstractScreenshotDiffUIContentProvider {

    public ReferenceToLatestScreenshotsUIProvider(@NotNull Project project) {
        super(project);
    }

    @Override
    public Component getContent(@NotNull ScreenshotDiff screenshotDiff) {
        Splitter referenceToLatestSplitView = null;
        if (screenshotDiff.hasLatest()) {
            referenceToLatestSplitView = new Splitter();
            referenceToLatestSplitView.setFirstComponent(createImageEditorFor(screenshotDiff.getOriginal()));
            //At this point screenshotDiff.getLatest() should not evaluate to null
            referenceToLatestSplitView.setSecondComponent(createImageEditorFor(screenshotDiff.getLatest()));
        }
        return referenceToLatestSplitView;
    }
}
