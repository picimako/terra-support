//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * Utility class for simplifying the retrieval of Terra wdio related data.
 */
final class TerraWdioDataRetrieverUtil {

    /**
     * Gets the {@link TerraWdioTree} object from the argument action event.
     */
    @Nullable
    static TerraWdioTree getWdioTreeFrom(@NotNull AnActionEvent e) {
        return (TerraWdioTree) e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
    }

    private TerraWdioDataRetrieverUtil() {
        //Utility class
    }
}
