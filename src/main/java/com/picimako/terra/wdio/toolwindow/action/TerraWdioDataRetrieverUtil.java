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
