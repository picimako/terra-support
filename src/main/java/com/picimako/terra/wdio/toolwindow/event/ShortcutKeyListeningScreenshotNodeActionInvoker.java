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

package com.picimako.terra.wdio.toolwindow.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.action.AbstractTerraWdioToolWindowAction;
import com.picimako.terra.wdio.toolwindow.action.CompareLatestWithReferenceScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.DeleteScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.NavigateToScreenshotUsageAction;
import com.picimako.terra.wdio.toolwindow.action.RenameScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.ReplaceReferenceWithLatestAction;
import com.picimako.terra.wdio.toolwindow.action.ShowDiffScreenshotsAction;

/**
 * Key event listener for the Terra wdio tool window tree nodes.
 * <p>
 * Validates that the used shortcut keys correspond to one of the available action's shortcut,
 * and if they do, it invokes that particular action.
 */
public final class ShortcutKeyListeningScreenshotNodeActionInvoker extends KeyAdapter {
    private static final Map<ShortcutKeyChecker, ActionProvider> ACTIONS = Map.of(
        RenameScreenshotsAction::isRenameScreenshotsShortcutKey, RenameScreenshotsAction::new,
        DeleteScreenshotsAction::isDeleteScreenshotsShortcutKey, DeleteScreenshotsAction::new,
        ReplaceReferenceWithLatestAction::isReplaceScreenshotsShortcutKey, ReplaceReferenceWithLatestAction::new,
        NavigateToScreenshotUsageAction::isNavigateToUsageShortcutKey, NavigateToScreenshotUsageAction::new,
        CompareLatestWithReferenceScreenshotsAction::isCompareLatestsWithReferencesShortcutKey, CompareLatestWithReferenceScreenshotsAction::new,
        ShowDiffScreenshotsAction::isShowDiffsShortcutKey, ShowDiffScreenshotsAction::new
    );

    private final Project project;
    private final TerraWdioTree tree;

    public ShortcutKeyListeningScreenshotNodeActionInvoker(@NotNull Project project, @NotNull TerraWdioTree tree) {
        this.project = project;
        this.tree = tree;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ACTIONS.keySet().stream()
            .filter(isShortcut -> isShortcut.test(e))
            .findFirst()
            .ifPresent(isShortcut -> ACTIONS.get(isShortcut).apply(project).validatePreconditionsAndPerformAction(tree));
    }

    @FunctionalInterface
    private interface ShortcutKeyChecker extends Predicate<KeyEvent> {
    }

    @FunctionalInterface
    private interface ActionProvider extends Function<Project, AbstractTerraWdioToolWindowAction> {
    }
}
