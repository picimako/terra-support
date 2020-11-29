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

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asSpec;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.isSpec;

import java.awt.*;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.intellij.util.PlatformIcons;
import icons.ImagesIcons;

/**
 * Tree cell rendered that configures each entry in the tree with an icon (at the left) and the name of the node (at the right).
 * <p>
 * The root node and specs are configured with a folder icon, while screenshots are customized with an image file icon.
 *
 * TODO: displaying/hiding the stats in the Terra wdio tool window could also be controlled by an IDE Settings property, e.g.
 *  *  {@code TerraSettings.getInstance().TERRA_WDIO_TOOL_WINDOW_STATS_ENABLED}.
 */
public class TerraWdioTreeCellRenderer extends DefaultTreeCellRenderer {

    public TerraWdioTreeCellRenderer() {
        setOpaque(false);
    }

    /**
     * {@inheritDoc}
     *
     * <b>Terra wdio tool window cell rendering</b>
     * <p>
     * Screenshot nodes are marked with bold text in case they have at least one diff image.
     * <p>
     * Spec nodes are also marked with bold text if one of their underlying screenshots has at least one diff image.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof TerraWdioTreeModelDataRoot) {
            setIcon(PlatformIcons.FOLDER_ICON);
            setFont(getFont().deriveFont(Font.PLAIN));
        } else if (isSpec(value)) {
            setIcon(PlatformIcons.FOLDER_ICON);
            potentiallyMarkAsDiff(() -> asSpec(value).getScreenshots().stream().anyMatch(TerraWdioTreeScreenshotNode::hasDiff));
        } else {
            setIcon(ImagesIcons.ImagesFileType);
            potentiallyMarkAsDiff(() -> asScreenshot(value).hasDiff());
        }
        setText(value.toString());

        return this;
    }

    private void potentiallyMarkAsDiff(Supplier<Boolean> hasDiff) {
        if (hasDiff.get()) {
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setFont(getFont().deriveFont(Font.PLAIN));
        }
    }
}