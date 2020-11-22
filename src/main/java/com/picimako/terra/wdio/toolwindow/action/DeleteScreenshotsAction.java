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

import static com.intellij.openapi.ui.Messages.YES;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.toolwindow.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.TerraWdioTreeSpecNode;

/**
 * An action to delete screenshot files via the Terra wdio tool window.
 * <p>
 * It deletes all available screenshots for the name the user selected in the tool window.
 */
public class DeleteScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    private static final String DELETE_SCREENSHOTS_TITLE = "Delete Screenshots";
    private static final String ARE_YOU_SURE_MESSAGE = "Are you sure you want to delete all screenshots with this name?";
    private static final String ERROR_DURING_DELETION_TITLE = "Error During Screenshot Deletion";
    private static final String COULD_NOT_DELETE_SCREENSHOTS_MESSAGE = "Could not delete the following screenshots:\n\n";

    /**
     * Creates a DeleteScreenshotsAction instance.
     * <p>
     * Also registers the common Delete shortcut key for this action.
     *
     * @param project the project
     */
    public DeleteScreenshotsAction(@NotNull Project project) {
        super(DELETE_SCREENSHOTS_TITLE, project);
        setShortcutSet(CommonShortcuts.getDelete());
    }

    /**
     * Handles the action when the user clicks the Delete Screenshots menu item.
     * <p>
     * First, it displays a modal dialog (with Yes/No options) asking the user to confirm if he/she indeed wants to
     * delete the images, and if the Yes button is clicked, then the deletion process takes place.
     * <p>
     * The deletion consists of two steps:
     * <ul>
     *     <li>first, deleting the selected screenshot files from the file system,</li>
     *     <li>then, if their removal was successful, deletes the corresponding node from the UI tree as well</li>
     * </ul>
     * <p>
     * If there is at least one screenshot that the action could not delete, then the platform shows a message dialog to the user,
     * containing the list of the files that could not be deleted.
     * <p>
     * If there is at least one file with the selected name that could not be deleted, the {@link com.intellij.openapi.vfs.VirtualFile} for
     * that file is still kept along with the respective screenshot node in the tool window.
     * <p>
     * TODO: there could also be an option to "Don't remind me anymore" which could be saved in Terra plugin settings
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            if (isUserSureToDeleteTheScreenshots()) {
                TerraWdioTreeScreenshotNode lastSelectedTreeNode = asScreenshot(tree.getLastSelectedPathComponent());
                final List<String> erroredFilePaths = new ArrayList<>();
                final List<VirtualFile> deletedScreenshotReferences = new ArrayList<>();

                for (VirtualFile reference : lastSelectedTreeNode.getReferences()) {
                    FileDeletionHandler.checkExistenceAndHandleDeletion(reference, this,
                        () -> deletedScreenshotReferences.add(reference),
                        () -> erroredFilePaths.add(reference.getPath()));
                }
                deletedScreenshotReferences.forEach(reference -> lastSelectedTreeNode.getReferences().remove(reference));

                //Delete the screenshot node from the tree model, and update the UI,
                // so that the changes are reflected in the tool window, otherwise show message dialog that deletion was not successful
                if (erroredFilePaths.isEmpty()) {
                    TerraWdioTreeSpecNode parentSpec = (TerraWdioTreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();
                    parentSpec.getScreenshots().remove(lastSelectedTreeNode);
                    tree.updateUI();
                } else {
                    Messages.showWarningDialog(project,
                        COULD_NOT_DELETE_SCREENSHOTS_MESSAGE + String.join("\n", erroredFilePaths),
                        ERROR_DURING_DELETION_TITLE);
                }
            }
        }
    }

    private boolean isUserSureToDeleteTheScreenshots() {
        return Messages.showYesNoDialog(project, ARE_YOU_SURE_MESSAGE, DELETE_SCREENSHOTS_TITLE, Messages.getQuestionIcon()) == YES;
    }

    /**
     * Gets whether the argument key event corresponds to this actions shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isDeleteScreenshotsShortcutKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DELETE;
    }
}
