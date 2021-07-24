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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
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
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
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
        return new AnAction[0];
    }
}
