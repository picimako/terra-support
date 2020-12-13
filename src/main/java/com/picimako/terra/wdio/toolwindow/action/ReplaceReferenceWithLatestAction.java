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

import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.toolwindow.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeSpecNode;

/**
 * An action to replace reference screenshot files with latest screenshots via the Terra wdio tool window.
 * <p>
 * It replaces all available screenshots for the name the user selected in the tool window.
 * <p>
 * NOTE: it might happen that the bold marking of a screenshot node (that it has diff image), and the status of this menu action
 * are not consistent. It can happen in cases when the diff and/or latest folders are modified manually.
 *
 * @since 0.1.0
 */
public class ReplaceReferenceWithLatestAction extends AbstractTerraWdioToolWindowAction {

    private static final String REPLACE_TITLE = "Replace Reference With Latest";
    private static final String COULD_NOT_REPLACE_SCREENSHOTS_MESSAGE = "Could not completely replace the following screenshots. "
        + "Please check your Version Control System for their current status:\n\n";
    private static final String ERROR_DURING_REPLACEMENT_TITLE = "Error During Replacement";

    /**
     * Creates a ReplaceReferenceWithLatestAction instance.
     * <p>
     * Also registers the common Replace shortcut key for this action.
     *
     * @param project the project
     */
    public ReplaceReferenceWithLatestAction(@NotNull Project project) {
        super(REPLACE_TITLE, project);
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_REPLACE);
        setShortcutSet(action.getShortcutSet());
    }

    /**
     * Handles the action when the user clicks the Replace Reference With Latest menu item.
     * <p>
     * Iterates through each latest image, then for each of them, finds the corresponding reference image (see {@link #relativePathEquals(String, String)}),
     * then first it deletes the reference image, and copies the latest image to its place. Finally, it deletes the corresponding diff images.
     * <p>
     * If there is at least one image that could not be deleted or copied, the list of affected image paths are listed in a message dialog.
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            TerraWdioTreeScreenshotNode selectedScreenshotNode = asScreenshot(tree.getLastSelectedPathComponent());

            final List<String> erroredFilePaths = new ArrayList<>();
            final Set<VirtualFile> referencesToRemove = new HashSet<>();
            final Set<VirtualFile> copiedFromLatests = new HashSet<>();
            final Set<VirtualFile> diffsToRemove = new HashSet<>();
            for (VirtualFile latestVf : selectedScreenshotNode.getLatests()) {
                selectedScreenshotNode.getReferences().stream()
                    .filter(referenceVf -> relativePathEquals(latestVf.getPath(), referenceVf.getPath()))
                    .findFirst()
                    .ifPresent(ref -> {
                        if (latestVf.exists()) {
                            try {
                                //First, remove the reference, then copy the latest to its place
                                WriteAction.run(() -> ref.delete(this));
                                referencesToRemove.add(ref);
                                WriteAction.run(() -> copiedFromLatests.add((latestVf.copy(this, ref.getParent(), latestVf.getName()))));
                                diffsToRemove.add(TerraWdioFolders.diffImageForLatest(project, latestVf));
                            } catch (IOException ioException) {
                                erroredFilePaths.add(ref.getPath());
                            }
                        }
                    });
            }

            referencesToRemove.forEach(ref -> selectedScreenshotNode.getReferences().remove(ref));
            copiedFromLatests.forEach(copied -> selectedScreenshotNode.getReferences().add(copied));
            diffsToRemove.forEach(diff -> {
                if (diff != null) {
                    FileDeletionHandler.checkExistenceAndHandleDeletion(diff, this,
                        () -> selectedScreenshotNode.getDiffs().remove(diff),
                        () -> erroredFilePaths.add(diff.getPath()));
                }
            });

            if (erroredFilePaths.isEmpty()) {
                TerraWdioTreeSpecNode parentSpec = (TerraWdioTreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();
                parentSpec.reorderScreenshotsAlphabeticallyByDisplayName();
                tree.updateUI();
            } else {
                Messages.showWarningDialog(project,
                    COULD_NOT_REPLACE_SCREENSHOTS_MESSAGE + String.join("\n", erroredFilePaths),
                    ERROR_DURING_REPLACEMENT_TITLE);
            }
        }
    }

    /**
     * Checks for the preconditions of this action, specifically that the selected screenshot has latest image.
     */
    @Override
    protected boolean meetsPreconditions(TerraWdioTree tree) {
        return asScreenshot(tree.getLastSelectedPathComponent()).hasLatest();
    }

    /**
     * Gets whether the path relative to the BASE_PATH/__snapshots__/&lt;latest or reference>/ is the same for the two
     * argument paths. That means that they essentially point to the same image name, matching the locale, browser,
     * viewport and spec as well.
     *
     * @param latestPath    the path to the latest version of the selected image
     * @param referencePath the path the reference version of the selected image
     * @return true if the relative path of the arguments match, false otherwise
     */
    private boolean relativePathEquals(String latestPath, String referencePath) {
        return latestPath.substring(latestPath.lastIndexOf(TerraWdioFolders.latestPath()) + TerraWdioFolders.latestPath().length())
            .equals(referencePath.substring(referencePath.lastIndexOf(TerraWdioFolders.referencePath()) + TerraWdioFolders.referencePath().length()));
    }

    /**
     * Gets whether the argument key event corresponds to this actions shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isReplaceScreenshotsShortcutKey(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R;
    }
}
