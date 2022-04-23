//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.projectview.action;

import static com.picimako.terra.wdio.ProblemDialogs.showNoValidationCallToNavigateToDialog;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioFolders.isInSnapshotsDirectory;
import static com.picimako.terra.wdio.TerraWdioFolders.isSnapshotsDirectory;
import static com.picimako.terra.wdio.TerraWdioFolders.specFolderIdentifier;

import java.util.Arrays;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.images.fileTypes.impl.ImageFileType;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.ProblemDialogs;
import com.picimako.terra.wdio.ToScreenshotUsageNavigator;

/**
 * An action to navigate to the usage of screenshots (within their corresponding wdio spec files) from file context menus
 * in the IDE Project View tool window.
 * <p>
 * This action is added right before the Find Usages action and bound to the Ctrl+B shortcut key.
 * <p>
 * It handles navigation to both default and non-default Terra screenshot validation calls.
 * <p>
 * The option is enabled only if the selected file is within a __snapshots__ directory, and that file is a binary file.
 * <p>
 * <b>Technical note</b>
 * Setting the wdio root path via TerraWdioFolders#setWdioTestRootPath is not necessary because this class doesn't use
 * any logic that requires it, since lookup of the spec file happens from the screenshot's direction instead of the wdio
 * root's direction.
 *
 * @see com.picimako.terra.wdio.toolwindow.action.NavigateToScreenshotUsageAction
 * @since 0.5.0
 */
public class NavigateToScreenshotUsageProjectViewAction extends AnAction {

    public NavigateToScreenshotUsageProjectViewAction() {
        super(TerraBundle.message("terra.wdio.project.view.screenshot.navigate.to.usage"), null, ImageFileType.INSTANCE.getIcon());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile selectedScreenshot = e.getData(CommonDataKeys.PSI_FILE);

        if (selectedScreenshot != null) {
            PsiDirectory snapshotsDirectory = (PsiDirectory) PsiTreeUtil.findFirstParent(
                selectedScreenshot,
                psiElement -> psiElement instanceof PsiDirectory && isSnapshotsDirectory((PsiDirectory) psiElement));

            if (snapshotsDirectory != null) {
                PsiDirectory specContainerFolder = snapshotsDirectory.getParent();

                if (specContainerFolder != null) {
                    //Since parent searching above has happened, at this point selectedScreenshot's parent should not be null
                    String specId = specFolderIdentifier(selectedScreenshot.getParent().getVirtualFile(), e.getProject());
                    Arrays.stream(specContainerFolder.getFiles())
                        .filter(file -> file.getVirtualFile().getNameWithoutExtension().equals(specId))
                        .findFirst()
                        .ifPresentOrElse(specFile -> {
                            if (!new ToScreenshotUsageNavigator(e.getProject()).navigateToUsage(specFile, selectedScreenshot.getName())) {
                                showNoValidationCallToNavigateToDialog();
                            }
                        }, ProblemDialogs::showNoSpecFileToNavigateToDialog);
                }
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isUsingTerra(e.getProject())
            && isInSnapshotsDirectory(e.getData(CommonDataKeys.VIRTUAL_FILE))
            && e.getData(CommonDataKeys.PSI_FILE) instanceof PsiBinaryFile);
    }
}
