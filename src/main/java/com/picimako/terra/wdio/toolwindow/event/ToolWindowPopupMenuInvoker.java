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

import static com.picimako.terra.wdio.toolwindow.event.TreeNodeTypeIdentifier.identifyTreeNodeTypeForClickLocation;

import java.awt.*;
import java.util.Map;

import com.intellij.openapi.actionSystem.ActionPopupMenu;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.TerraWdioTree;

/**
 * This class takes in the {@link TerraWdioTree} instance and a set of {@link ActionPopupMenu}s identified by their node
 * types, and for any selected node, it displays the proper action popup menu.
 * <p>
 * The logic for what mouse or shortcut keys to display the popups is handled in separate listener implementations.
 */
public final class ToolWindowPopupMenuInvoker {
    private final TerraWdioTree tree;
    private final Map<String, ActionPopupMenu> actionPopupMenus;

    public ToolWindowPopupMenuInvoker(@NotNull TerraWdioTree tree, @NotNull Map<String, ActionPopupMenu> actionPopupMenus) {
        this.tree = tree;
        this.actionPopupMenus = actionPopupMenus;
    }

    //Invokes the context menu only when it is initiated on the proper node type
    public void invokePopup(Component comp, int x, int y) {
        String nodeTypeToSelect = identifyTreeNodeTypeForClickLocation(tree, x, y);
        if (nodeTypeToSelect != null) {
            actionPopupMenus.get(nodeTypeToSelect).getComponent().show(comp, x, y);
        }
    }
}
