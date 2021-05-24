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

package com.picimako.terra.wdio.toolwindow.event;

import javax.swing.tree.TreePath;

import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;

/**
 * Utility class to retrieve the node type selected in the Terra wdio tool window.
 */
final class TreeNodeTypeIdentifier {

    /**
     * Gets the node type as String, for the selected node in the Terra wdio tool window.
     *
     * @param tree the terra wdio tree
     * @param x    the x coordinate where the click happened in the tree
     * @param y    the y coordinate where the click happened in the tree
     * @return "Screenshot" for screenshot nodes, and "Spec" for spec nodes
     */
    static String identifyTreeNodeTypeForClickLocation(TerraWdioTree tree, int x, int y) {
        String itemToSelect = null;
        TreePath path = tree.getPathForLocation(x, y);
        if (path != null) {
            tree.setSelectionPath(path);
            int selectionRow = tree.getRowForLocation(x, y);
            if (selectionRow > -1) {
                tree.setSelectionRow(selectionRow);
            }
            //Indicates that this particular action popup should be displayed only for a screenshot or a spec node in the tree
            if (path.getLastPathComponent() instanceof TreeScreenshotNode) {
                itemToSelect = "Screenshot";
//              } else if (path.getLastPathComponent() instanceof TerraWdioTreeSpecNode) {
//                  itemToSelect = "Spec";
            }
        }
        return itemToSelect;
    }
}
