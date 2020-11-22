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

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

/**
 * A container panel for storing the different screenshot diff view components in a vertically scrollable view.
 * <p>
 * Each diff image view is extended with screenshot context information containing the locale, browser and viewport.
 */
public class TerraScreenshotsDiffViewContainer extends JPanel {

    private TerraScreenshotsDiffViewContainer() {
        add(new JLabel("There is no screenshot available to display."));
    }

    public TerraScreenshotsDiffViewContainer(@NotNull List<ScreenshotDiff> screenshotDiffs, @NotNull ScreenshotDiffUIContentProvider uiProvider) {
        setLayout(new BorderLayout());
        JPanel previewContent = new JPanel();
        previewContent.setLayout(new BoxLayout(previewContent, BoxLayout.Y_AXIS));

        for (ScreenshotDiff diff : screenshotDiffs) {
            Component diffView = uiProvider.getContent(diff);
            if (diffView != null) {
                previewContent.add(screenshotContextLabelFor(diff));
                previewContent.add(diffView);
            }
        }

        //HORIZONTAL_SCROLLBAR_NEVER makes sure that the images stay within the viewport and don't slip out
        JBScrollPane scrollPane = new JBScrollPane(previewContent, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    @NotNull
    private JLabel screenshotContextLabelFor(ScreenshotDiff screenshotDiff) {
        JLabel contextLabel = new JLabel(new ScreenshotContextParser().parse(screenshotDiff.getOriginal().getPath()));
        contextLabel.setFont(getFont().deriveFont(16f));
        contextLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //https://stackoverflow.com/questions/2560784/how-to-center-elements-in-the-boxlayout-using-center-of-the-element
        return contextLabel;
    }

    /**
     * Provides an empty panel with a message saying no screenshots are available.
     */
    public static TerraScreenshotsDiffViewContainer noScreenshotAvailable() {
        return new TerraScreenshotsDiffViewContainer();
    }
}
