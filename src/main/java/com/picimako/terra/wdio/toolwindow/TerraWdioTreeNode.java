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

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node in the {@link TerraWdioTree} which then is visualized within the Terra wdio tool window as a tree.
 *
 * @see TerraWdioTreeModelDataRoot
 * @see TerraWdioTreeSpecNode
 * @see TerraWdioTreeScreenshotNode
 */
public interface TerraWdioTreeNode extends Disposable {

    /**
     * Returns the name of the spec file (without the -spec.js ending), or the screenshot (including the extension).
     *
     * @return the spec folder or screenshot file name
     */
    String getDisplayName();

    /**
     * Creates a TerraWdioTreeSpecNode configured as a spec file.
     *
     * @param displayName the displayName of the spec file
     * @return the new node
     */
    static TerraWdioTreeSpecNode forSpec(@NotNull String displayName) {
        return new TerraWdioTreeSpecNode(displayName);
    }

    /**
     * Creates a TerraWdioTreeScreenshotNode configured as a screenshot.
     *
     * @param displayName the displayName of the screenshot (without the extension)
     * @return the new node
     */
    static TerraWdioTreeScreenshotNode forScreenshot(@NotNull String displayName) {
        return new TerraWdioTreeScreenshotNode(displayName);
    }

    /**
     * Gets whether the argument node corresponds to a spec.
     *
     * @param node the node the check
     * @return true if the node is a spec, otherwise false
     */
    static boolean isSpec(Object node) {
        return node instanceof TerraWdioTreeSpecNode;
    }

    /**
     * Returns the argument object as a {@link TerraWdioTreeSpecNode}.
     *
     * @param node the object to cast
     * @return the casted object
     */
    static TerraWdioTreeSpecNode asSpec(Object node) {
        return (TerraWdioTreeSpecNode) node;
    }

    /**
     * Gets whether the argument node corresponds to a screenshot.
     *
     * @param node the node the check
     * @return true if the node is a screenshot, otherwise false
     */
    static boolean isScreenshot(Object node) {
        return node instanceof TerraWdioTreeScreenshotNode;
    }

    /**
     * Returns the argument object as a {@link TerraWdioTreeScreenshotNode}.
     *
     * @param node the object to cast
     * @return the casted object
     */
    static TerraWdioTreeScreenshotNode asScreenshot(Object node) {
        return (TerraWdioTreeScreenshotNode) node;
    }
}
