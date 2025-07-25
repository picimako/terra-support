//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * Provides easier implementation of, and a somewhat updated API for performing Terra wdio tool window actions.
 *
 * @since 0.1.0
 */
public abstract class AbstractTerraWdioToolWindowAction extends AnAction {

    public static AbstractTerraWdioToolWindowAction getAction(String actionId) {
        return (AbstractTerraWdioToolWindowAction) ActionManager.getInstance().getAction(actionId);
    }

    //Performing the action

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        performAction(getWdioTreeFrom(e), e.getProject());
    }

    /**
     * Performs the action.
     * <p>
     * The argument {@code Project} object most probably comes from an action event.
     *
     * @param tree    the wdio tree where this action is invoked on
     * @param project the project
     */
    public abstract void performAction(TerraWdioTree tree, @Nullable Project project);

    //Preconditions and update logic

    /**
     * Validates the preconditions for this action, then if they are met, performs this action.
     *
     * @param tree    the tree to perform the action on
     */
    public void validatePreconditionsAndPerformAction(TerraWdioTree tree, @NotNull Project project) {
        if (meetsPreconditions(tree)) {
            performAction(tree, project);
        }
    }

    /**
     * Checks for the preconditions of this action.
     * <p>
     * The default value is true since there might be actions that are always enabled, and don't require any additional
     * checks for enabling/disabling it.
     *
     * @param tree the tree to query for the selected node
     * @return true if this action meets the preconditions, false otherwise
     */
    protected boolean meetsPreconditions(TerraWdioTree tree) {
        return true;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(meetsPreconditions(getWdioTreeFrom(e)));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    //Helpers

    /**
     * Gets the {@link TerraWdioTree} object from the argument action event.
     */
    @Nullable
    private static TerraWdioTree getWdioTreeFrom(@NotNull AnActionEvent e) {
        var data = e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
        return data instanceof TerraWdioTree ? (TerraWdioTree) data : null;
    }
}
