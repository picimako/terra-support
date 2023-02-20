//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;

import java.util.List;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
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
 *
 * @see TerraWdioScreenshotsPanel
 * @since 0.1.0
 */
public class TerraWdioToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var screenshotsPanel = new TerraWdioScreenshotsPanel(project);
        addTab(toolWindow, screenshotsPanel);
        toolWindow.setTitleActions(List.of(
            new FindUnusedScreenshotsAction(screenshotsPanel.getTree(), project),
            new ToggleStatisticsAction(() -> screenshotsPanel.getTree().updateUI())
        ));
    }

    private void addTab(ToolWindow toolWindow, JComponent component) {
        var contentManager = toolWindow.getContentManager();
        var content = contentManager.getFactory().createContent(component, null, true);
        contentManager.addContent(content);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        if (!isUsingTerra(project)) return false;

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
