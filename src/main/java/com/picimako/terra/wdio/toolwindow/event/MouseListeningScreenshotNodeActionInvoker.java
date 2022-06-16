//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import com.intellij.openapi.project.Project;

import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.action.CompareLatestWithReferenceScreenshotsAction;

/**
 * Adds support for the Screenshot nodes in the Terra wdio tree to be able to open the Reference/Latest Preview
 * by double-clicking on screenshot nodes.
 */
public class MouseListeningScreenshotNodeActionInvoker extends MouseAdapter {
    private final Project project;
    private final TerraWdioTree tree;

    public MouseListeningScreenshotNodeActionInvoker(Project project, TerraWdioTree tree) {
        this.project = project;
        this.tree = tree;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            String nodeType = TreeNodeTypeIdentifier.identifyTreeNodeTypeForClickLocation(tree, e.getX(), e.getY());
            if ("Screenshot".equals(nodeType)) {
                new CompareLatestWithReferenceScreenshotsAction(project).performAction(tree);
            }
        }
    }
}
