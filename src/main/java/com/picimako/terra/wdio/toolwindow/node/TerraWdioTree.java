//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
import org.intellij.images.fileTypes.impl.ImageFileType;
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
         * Screenshot nodes are marked with a dedicated diff icon in case they have at least one diff image.
         * <p>
         * Spec nodes are also marked with the same diff icon when one of their underlying screenshots has at least one diff image.
         */
        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof TreeModelDataRoot) {
                setIcon(PlatformIcons.FOLDER_ICON);
                setFont(getFont().deriveFont(Font.PLAIN));
            } else if (isSpec(value)) {
                setIcon(
                    asSpec(value).getScreenshots().stream().anyMatch(TreeScreenshotNode::hasDiff),
                    asSpec(value).getScreenshots().stream().anyMatch(TreeScreenshotNode::isUnused),
                    PlatformIcons.FOLDER_ICON);
            } else if (isScreenshot(value)) {
                setIcon(asScreenshot(value).hasDiff(), asScreenshot(value).isUnused(), ImageFileType.INSTANCE.getIcon());
            }
        }

        /**
         * If the current node is unused, or has an unused child node, it is marked with an error icon. This takes precedence.
         * <p>
         * Otherwise, if there is a diff image for the node, then it is marked with a diff icon. In every other case, the node's default icon is used.
         */
        private void setIcon(boolean hasDiff, boolean isOrHasUnused, Icon defaultIcon) {
            setIcon(isOrHasUnused
                ? AllIcons.RunConfigurations.TestError
                : hasDiff ? AllIcons.Actions.Diff : defaultIcon);
        }
    }
}
