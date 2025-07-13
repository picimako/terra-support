//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.event;

import static com.picimako.terra.wdio.toolwindow.action.AbstractTerraWdioToolWindowAction.getAction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public final class ShortcutKeyListeningScreenshotNodeActionInvoker extends KeyAdapter {
    private static final Map<ShortcutKeyChecker, Supplier<AbstractTerraWdioToolWindowAction>> ACTIONS = Map.of(
        DeleteScreenshotsAction::isDeleteScreenshotsShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.DeleteScreenshotsAction"),
        RenameScreenshotsAction::isRenameScreenshotsShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.RenameScreenshotsAction"),
        ReplaceReferenceWithLatestAction::isReplaceScreenshotsShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.ReplaceReferenceWithLatestAction"),
        NavigateToScreenshotUsageAction::isNavigateToUsageShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.NavigateToScreenshotUsageAction"),
        CompareLatestWithReferenceScreenshotsAction::isCompareLatestsWithReferencesShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.CompareLatestWithReferenceScreenshotsAction"),
        ShowDiffScreenshotsAction::isShowDiffsShortcutKey, () -> getAction("com.picimako.terra.wdio.toolwindow.action.ShowDiffScreenshotsAction")
    );

    @NotNull
    private final Project project;
    @NotNull
    private final TerraWdioTree tree;

    @Override
    public void keyPressed(KeyEvent e) {
        ACTIONS.keySet().stream()
            .filter(isShortcut -> isShortcut.test(e))
            .findFirst()
            .ifPresent(isShortcut -> ACTIONS.get(isShortcut).get().validatePreconditionsAndPerformAction(tree, project));
    }

    @FunctionalInterface
    private interface ShortcutKeyChecker extends Predicate<KeyEvent> {
    }
}
