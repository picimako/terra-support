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
