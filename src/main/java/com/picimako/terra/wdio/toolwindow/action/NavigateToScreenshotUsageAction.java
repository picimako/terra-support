/*
 * Copyright 2021 Tamás Balog
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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.BuildNumberHelper.isIDEBuildNumberSameOrNewerThan;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.screenshot.TerraScreenshotNameResolver;
import com.picimako.terra.wdio.toolwindow.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeModel;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeSpecNode;

/**
 * An action to navigate to the usage of screenshots (within their corresponding wdio spec files) from the Terra
 * wdio tool window.
 * <p>
 * It handles navigation to both default and non-default Terra screenshot validation calls.
 *
 * @since 0.4.0
 */
public class NavigateToScreenshotUsageAction extends AbstractTerraWdioToolWindowAction {

    private final TerraScreenshotNameResolver resolver = new TerraScreenshotNameResolver();

    /**
     * Creates a NavigateToScreenshotUsageAction instance.
     * <p>
     * Also registers the common Go to Declaration shortcut key for this action.
     */
    public NavigateToScreenshotUsageAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("screenshot.navigate.to.usage"), project);
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_GOTO_DECLARATION);
        setShortcutSet(action.getShortcutSet());
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
            String selectedScreenshotNodeName = asScreenshot(tree.getLastSelectedPathComponent()).getDisplayName();
            TerraWdioTreeSpecNode parentSpec = (TerraWdioTreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();
            if (TerraWdioTreeModel.existsAfterRefresh(parentSpec.getSpecFile())) {
                NavigationStatusHolder status = new NavigationStatusHolder();
                PsiFile specPsiFile = PsiManager.getInstance(project).findFile(parentSpec.getSpecFile());

                if (isIDEBuildNumberSameOrNewerThan("202.5103.13")) {
                    PsiTreeUtil.processElements(specPsiFile, JSCallExpression.class, element -> !hasNavigatedToUsage(selectedScreenshotNodeName, element, status));
                } else {
                    PsiTreeUtil.processElements(specPsiFile, element -> {
                        if (element instanceof JSCallExpression) {
                            return !hasNavigatedToUsage(selectedScreenshotNodeName, (JSCallExpression) element, status);
                        }
                        return true;
                    });
                }

                if (!status.hasNavigated) {
                    Messages.showWarningDialog(
                        TerraBundle.toolWindow("screenshot.navigate.to.usage.no.validation.call.message"),
                        TerraBundle.toolWindow("screenshot.navigate.to.usage.no.validation.call.title"));
                }
            } else {
                Messages.showWarningDialog(
                    TerraBundle.toolWindow("screenshot.navigate.to.usage.no.spec.file.message"),
                    TerraBundle.toolWindow("screenshot.navigate.to.usage.no.spec.file.title"));
            }
        }
    }

    private boolean hasNavigatedToUsage(String selectedScreenshotNodeName, JSCallExpression callExpression, NavigationStatusHolder status) {
        if (isScreenshotValidationCall(callExpression)) {
            JSLiteralExpression nameExpr = getFirstArgumentAsStringLiteral(callExpression.getArgumentList());
            String resolvedScreenshotName = resolver.resolveWithFallback(nameExpr, callExpression.getMethodExpression());

            if (selectedScreenshotNodeName.equals(resolvedScreenshotName)) {
                if (nameExpr != null) {
                    nameExpr.navigate(true);
                } else {
                    callExpression.getMethodExpression().navigate(true);
                }
                status.hasNavigated = true;
                return true;
            }
        }
        return false;
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

    private static final class NavigationStatusHolder {
        boolean hasNavigated = false;
    }
}
