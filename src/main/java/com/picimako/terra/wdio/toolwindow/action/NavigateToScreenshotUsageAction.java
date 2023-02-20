//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.ProblemDialogs.showNoSpecFileToNavigateToDialog;
import static com.picimako.terra.wdio.ProblemDialogs.showNoValidationCallToNavigateToDialog;
import static com.picimako.terra.wdio.TerraWdioFolders.existsAfterRefresh;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.ToScreenshotUsageNavigator;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * An action to navigate to the usage of screenshots (within their corresponding wdio spec files) from the Terra
 * wdio tool window.
 * <p>
 * It handles navigation to both default and non-default Terra screenshot validation calls.
 *
 * @see com.picimako.terra.wdio.projectview.action.NavigateToScreenshotUsageProjectViewAction
 * @since 0.4.0
 */
public class NavigateToScreenshotUsageAction extends AbstractTerraWdioToolWindowAction {

    private final ToScreenshotUsageNavigator navigator;

    /**
     * Creates a NavigateToScreenshotUsageAction instance.
     * <p>
     * Also registers the common Go to Declaration shortcut key for this action.
     */
    public NavigateToScreenshotUsageAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("screenshot.navigate.to.usage"), project);
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_GOTO_DECLARATION);
        setShortcutSet(action.getShortcutSet());
        navigator = new ToScreenshotUsageNavigator(project);
    }

    /**
     * Handles the action when the user clicks the Navigate to Usage menu item.
     * <p>
     * It retrieves the spec file related to the selected screenshot, then iterates through all JS call expressions
     * in the file. When it finds a Terra screenshot validation call whose parameterization (name and parent describe block name)
     * matches the screenshot's name, then it opens the spec file and positions the caret to the proper element.
     * <p>
     * If the file has already been open, then it just focuses on that editor, and then positions the caret.
     * <p>
     * At end of the navigation logic it may show a warning dialog in the following circumstances:
     * <ul>
     *     <li>There is no spec file to open. This can occur when a spec file has been deleted after or wasn't available at the IDE startup,
     *     but the screenshots linked screenshots are still available in the __snapshots__ directory.</li>
     *     <li>There is no screenshot validation linked to this image to navigate to. This can occur when the Terra validation call for this
     *     image was removed entirely or it references this image by a different name.</li>
     * </ul>
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            var parentSpec = (TreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();
            if (existsAfterRefresh(parentSpec.getSpecFile())) {
                var specPsiFile = PsiManager.getInstance(project).findFile(parentSpec.getSpecFile());
                String selectedScreenshotNodeName = asScreenshot(tree.getLastSelectedPathComponent()).getDisplayName();
                if (!navigator.navigateToUsage(specPsiFile, selectedScreenshotNodeName))
                    showNoValidationCallToNavigateToDialog();
            } else
                showNoSpecFileToNavigateToDialog();
        }
    }

    /**
     * Gets whether the argument key event corresponds to this action's shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isNavigateToUsageShortcutKey(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B;
    }
}
