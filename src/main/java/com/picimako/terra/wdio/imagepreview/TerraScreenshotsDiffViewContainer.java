//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraResourceManager;

/**
 * A container panel for storing the different screenshot diff view components in a vertically scrollable view.
 * <p>
 * Each diff image view is extended with screenshot context information containing the locale, browser and viewport.
 */
public class TerraScreenshotsDiffViewContainer extends JPanel {

    private Project project;

    private TerraScreenshotsDiffViewContainer() {
        add(new JLabel("There is no screenshot available to display."));
    }

    public TerraScreenshotsDiffViewContainer(@NotNull List<ScreenshotDiff> screenshotDiffs, @NotNull ScreenshotDiffUIContentProvider uiProvider, Project project) {
        this.project = project;
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
        JLabel contextLabel = new JLabel(TerraResourceManager.getInstance(project)
            .screenshotContextParser()
            .parse(screenshotDiff.getOriginal().getPath()));
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
