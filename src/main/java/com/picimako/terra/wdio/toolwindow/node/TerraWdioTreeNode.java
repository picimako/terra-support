//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.node;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node in the {@link TerraWdioTree} which then is visualized within the Terra wdio tool window as a tree.
 *
 * @see TreeModelDataRoot
 * @see TreeSpecNode
 * @see TreeScreenshotNode
 */
public interface TerraWdioTreeNode extends Disposable {

    /**
     * Returns the name of the spec file (without the -spec.js ending), or the screenshot (including the extension).
     */
    String getDisplayName();

    /**
     * Creates a TerraWdioTreeSpecNode configured as a spec file.
     *
     * @param displayName the displayName of the spec file
     * @return the new node
     */
    static TreeSpecNode forSpec(@NotNull String displayName, Project project) {
        return new TreeSpecNode(displayName, project);
    }

    /**
     * Creates a TerraWdioTreeScreenshotNode configured as a screenshot.
     *
     * @param displayName the displayName of the screenshot (without the extension)
     * @return the new node
     */
    static TreeScreenshotNode forScreenshot(@NotNull String displayName, Project project) {
        return new TreeScreenshotNode(displayName, project);
    }

    /**
     * Gets whether the argument node corresponds to a spec.
     *
     * @param node the node the check
     * @return true if the node is a spec, otherwise false
     */
    static boolean isSpec(Object node) {
        return node instanceof TreeSpecNode;
    }

    /**
     * Returns the argument object as a {@link TreeSpecNode}.
     *
     * @param node the object to cast
     * @return the cast object
     */
    static TreeSpecNode asSpec(Object node) {
        return (TreeSpecNode) node;
    }

    /**
     * Gets whether the argument node corresponds to a screenshot.
     *
     * @param node the node the check
     * @return true if the node is a screenshot, otherwise false
     */
    static boolean isScreenshot(Object node) {
        return node instanceof TreeScreenshotNode;
    }

    /**
     * Returns the argument object as a {@link TreeScreenshotNode}.
     *
     * @param node the object to cast
     * @return the cast object
     */
    static TreeScreenshotNode asScreenshot(Object node) {
        return (TreeScreenshotNode) node;
    }
}
