/*
 * Copyright 2021 Tamás Balog
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

package com.picimako.terra.wdio.toolwindow.node;

import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asSpec;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isSpec;

import java.awt.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.util.PlatformIcons;
import icons.ImagesIcons;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.TerraWdioTreeModel;

/**
 * A custom Swing component for rendering a tree view for the Terra wdio components.
 * <p>
 * It uses the {@link TerraWdioNodeRenderer} to customize the appearance of the tree nodes.
 *
 * @see TerraWdioTreeModel
 */
public class TerraWdioTree extends JTree {

    /**
     * Creates a tree component.
     *
     * @param model the underlying model containing the nodes of the tree
     */
    public TerraWdioTree(TreeModel model) {
        super(model);
        setCellRenderer(new TerraWdioNodeRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setExpandsSelectedPaths(true);
    }

    /**
     * Gets the parent node of the currently selected node as spec.
     * <p>
     * Call this only when you are sure that the currently selected node is a screenshot node.
     */
    public TreeSpecNode getParentSpecOfSelected() {
        return asSpec(getSelectionPath().getParentPath().getLastPathComponent());
    }

    /**
     * Gets the root node of this tree.
     */
    public TreeModelDataRoot getRoot() {
        return (TreeModelDataRoot) getModel().getRoot();
    }

    /**
     * Gets all expanded nodes of this tree, the expanded descendants of the root node.
     */
    public Enumeration<TreePath> getAllExpandedNodes() {
        return getExpandedDescendants(new TreePath(getRoot()));
    }

    /**
     * Restores the expansion state of nodes to a previous state (the argument expanded nodes).
     *
     * @param expandedNodes the previous state of nodes
     */
    public void restoreExpansionStateFrom(Enumeration<TreePath> expandedNodes) {
        while (expandedNodes.hasMoreElements()) {
            expandPath(expandedNodes.nextElement());
        }
    }

    /**
     * Tree cell rendered that configures each entry in the tree with an icon (at the left) and the name of the node (at the right).
     * <p>
     * The root node and specs are configured with a folder icon, while screenshots are customized with an image file icon.
     */
    public static final class TerraWdioNodeRenderer extends NodeRenderer {

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
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof TreeModelDataRoot) {
                setIcon(PlatformIcons.FOLDER_ICON);
                setFont(getFont().deriveFont(Font.PLAIN));
            } else if (isSpec(value)) {
                potentiallyMarkAsDiff(asSpec(value).getScreenshots().stream().anyMatch(TreeScreenshotNode::hasDiff));
                markAsUnusedOrDefault(asSpec(value).getScreenshots().stream().anyMatch(TreeScreenshotNode::isUnused), PlatformIcons.FOLDER_ICON);
            } else if (isScreenshot(value)) {
                potentiallyMarkAsDiff(asScreenshot(value).hasDiff());
                markAsUnusedOrDefault(asScreenshot(value).isUnused(), ImagesIcons.ImagesFileType);
            }
        }

        private void potentiallyMarkAsDiff(boolean hasDiff) {
            setFont(getFont().deriveFont(hasDiff ? Font.BOLD : Font.PLAIN));
        }

        private void markAsUnusedOrDefault(boolean isOrHasUnused, Icon defaultIcon) {
            setIcon(isOrHasUnused ? AllIcons.RunConfigurations.TestError : defaultIcon);
        }
    }
}
