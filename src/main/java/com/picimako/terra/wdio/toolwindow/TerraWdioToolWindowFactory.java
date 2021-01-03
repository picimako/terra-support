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

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import javax.swing.*;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.BuildNumber;
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
 * @see TerraWdioScreenshotsPanel
 * @since 0.1.0
 */
public class TerraWdioToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        TerraWdioScreenshotsPanel screenshotsPanel = new TerraWdioScreenshotsPanel(project);
        addTab(toolWindow, screenshotsPanel);
        if (isSetTitleActionSupported()) {
            toolWindow.setTitleActions(singletonList(new FindUnusedScreenshotsAction(screenshotsPanel.getTree())));
        }
    }

    private void addTab(ToolWindow toolWindow, JComponent component) {
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(component, null, true);
        contentManager.addContent(content);
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

    private boolean isSetTitleActionSupported() {
        return ApplicationInfo.getInstance().getBuild().compareTo(requireNonNull(BuildNumber.fromString("202.5103.13"))) >= 0;
    }
}
