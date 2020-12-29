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

import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asSpec;

import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A custom Swing component for rendering a tree view for the Terra wdio components.
 * <p>
 * It uses the {@link TerraWdioTreeCellRenderer} to customize the appearance of the tree nodes.
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
        setCellRenderer(new TerraWdioTreeCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setExpandsSelectedPaths(true);
    }

    /**
     * Gets the parent node of the currently selected node as spec.
     * <p>
     * Call this only when you are sure that the currently selected node is a screenshot node.
     *
     * @return the parent spec node
     */
    public TerraWdioTreeSpecNode getParentSpec() {
        return asSpec(getSelectionPath().getParentPath().getLastPathComponent());
    }

    /**
     * Gets the root node of this tree.
     *
     * @return the root node
     */
    public TerraWdioTreeModelDataRoot getRoot() {
        return (TerraWdioTreeModelDataRoot) getModel().getRoot();
    }

    /**
     * Gets all expanded nodes of this tree, the expanded descendants of the root node.
     *
     * @return the expanded nodes
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
}
