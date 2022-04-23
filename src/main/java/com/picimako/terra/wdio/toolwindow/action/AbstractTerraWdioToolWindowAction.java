//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.toolwindow.action.TerraWdioDataRetrieverUtil.getWdioTreeFrom;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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

    protected final Project project;

    protected AbstractTerraWdioToolWindowAction(String text, @NotNull Project project) {
        super(text);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        performAction(getWdioTreeFrom(e), e.getProject());
    }

    /**
     * Same as {@link #performAction(TerraWdioTree, Project)} with the difference that this uses the preconfigured
     * {@link Project} object instead of one from an action event.
     *
     * @param tree the wdio tree where this action is invoked on
     */
    public void performAction(TerraWdioTree tree) {
        performAction(tree, project);
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

    /**
     * Validates the preconditions for this action, then if they are met, performs this action.
     *
     * @param tree the tree to perform the action on
     */
    public void validatePreconditionsAndPerformAction(TerraWdioTree tree) {
        if (meetsPreconditions(tree)) {
            performAction(tree);
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
}
