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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Creates the contents of the Terra wdio tool window.
 * <p>
 * The tool window is enabled only when the following conditions are met:
 * <ul>
 *     <li>the project is not a default project</li>
 *     <li>one of the wdio test root folders is present in the project</li>
 * </ul>
 * <p>
 * Since this class determines whether to display the tool window or not, when it is determined that it will be shown,
 * it also initializes {@link TerraWdioFolders} with the wdio test root path that will be used as the root path throughout
 * the entire lifecycle of this feature in a particular project.
 * <p>
 * TODO: the availability of this tool window could also be controlled by an IDE Settings property, e.g.
 *  {@code TerraSettings.getInstance().TERRA_WDIO_TOOL_WINDOW_ENABLED}.
 *
 * @see TerraWdioToolWindowPanel
 * @since 0.1.0
 */
public class TerraWdioToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        TerraWdioToolWindowPanel panel = new TerraWdioToolWindowPanel(project);
        Content content = contentManager.getFactory().createContent(panel, null, true);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        boolean shouldBeAvailable = !project.isDefault();
        if (shouldBeAvailable) {
            VirtualFile wdioRoot = TerraWdioFolders.projectWdioRoot(project);
            shouldBeAvailable = wdioRoot != null && wdioRoot.exists();
            if (shouldBeAvailable) {
                TerraWdioFolders.setWdioTestRootPath(wdioRoot.getPath().replace(project.getBasePath(), ""));
            }
        }
        return shouldBeAvailable;
    }
}
