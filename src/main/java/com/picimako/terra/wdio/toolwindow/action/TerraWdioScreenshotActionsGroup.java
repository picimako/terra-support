//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom {@link com.intellij.openapi.actionSystem.ActionGroup} for displaying popup menu actions for screenshot nodes
 * in the Terra wdio tool window.
 * <p>
 * For the list of displayed actions please see {@link #getChildren(AnActionEvent)}.
 *
 * @since 0.1.0
 */
public class TerraWdioScreenshotActionsGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project != null) {
            return new AnAction[]{
                //Alteration actions
                new DeleteScreenshotsAction(project),
                new RenameScreenshotsAction(project),
                new ReplaceReferenceWithLatestAction(project),
                Separator.getInstance(),
                //Navigation actions
                new NavigateToScreenshotUsageAction(project),
                Separator.getInstance(),
                //Comparison actions
                new CompareLatestWithReferenceScreenshotsAction(project),
                new ShowDiffScreenshotsAction(project)};
        }
        return AnAction.EMPTY_ARRAY;
    }
}
