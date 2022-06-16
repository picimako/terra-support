//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.io.IOException;

import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * An action to rename screenshot files via the Terra wdio tool window.
 * <p>
 * It renames all available screenshots for the name the user selected in the tool window.
 *
 * @since 0.1.0
 */
public class RenameScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    /**
     * Creates a RenameScreenshotsAction instance.
     * <p>
     * Also registers the common Rename shortcut key for this action.
     *
     * @param project the project
     */
    public RenameScreenshotsAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("rename.screenshots"), project);
        setShortcutSet(CommonShortcuts.getRename());
    }

    /**
     * Handles the action when the user clicks the Rename Screenshots menu item.
     * <p>
     * First, asks for a new filename from the user, and if the new name is different from the original, then the renaming
     * can take place.
     * <p>
     * It iterates through all underlying {@link VirtualFile} instances in the screenshot node this rename happens for, and does
     * the following steps:
     * <ul>
     *     <li>invokes the deletion of the file on the file system. If the deletion fails, saves the path of the file for later
     *     notification. If the deletion completes, proceeds with the steps below.</li>
     *     <li>instead of doing the rename in the same screenshot node, it creates a separate screenshot node for the file with the new name,
     *     into which it moves the successfully renamed files one-by-one,</li>
     *     <li>then, after it iterated through all necessary virtual files, it removes them from the original screenshot node,
     *     and deletes that node, so that those virtual files will live on in the newly created node.</li>
     * </ul>
     * <p>
     * It also shows warning dialogs when either at least one virtual file referenced by the selected screenshot node is non-writable,
     * or when at least one screenshot could not be renamed. In the latter case it also lists the affected files' paths.
     * <p>
     * NOTE: this action doesn't rename the values within spec JS files (describe and Terra.validates blocks and calls). That has to be done manually.
     * <p>
     * NOTE 2: the rename action works in a way that it first deletes the selected file, then creates a new one with the new name.
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            var selectedScreenshotNode = asScreenshot(tree.getLastSelectedPathComponent());
            String originalFileName = selectedScreenshotNode.getDisplayName();
            String newFileName = askUserForNewFileName(originalFileName, originalFileName.substring(originalFileName.lastIndexOf(".") + 1));

            if (newFileName == null) return; //The user didn't click Cancel
            var references = selectedScreenshotNode.getReferences();

            if (references.stream().allMatch(VirtualFile::isWritable)) {
                final var successfullyRenamed = new SmartList<VirtualFile>();
                final var erroredFilePaths = new SmartList<String>();
                var parentSpec = (TreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();

                for (var reference : references) {
                    if (!reference.exists()) continue;
                    try {
                        //Rename the actual screenshot files on the file system
                        WriteAction.run(() -> reference.rename(this, newFileName));
                        //If not yet created, create a new Screenshot Node under the current parent Spec Node, with the new name
                        if (!parentSpec.hasScreenshotNodeForName(newFileName)) {
                            TreeScreenshotNode newScreenshot = TerraWdioTreeNode.forScreenshot(newFileName, project);
                            newScreenshot.addReference(reference);
                            parentSpec.addScreenshot(newScreenshot);
                        } else {
                            //Add the current VirtualFile to the new Screenshot Node
                            parentSpec.findScreenshotNodeByName(newFileName).get().addReference(reference);
                        }
                        //Mark the current VirtualFile to be removed from the current Screenshot Node (they will be removed later in bulk)
                        successfullyRenamed.add(reference);
                    } catch (IOException ioe) {
                        erroredFilePaths.add(reference.getPath());
                    }
                }

                //Remove all VirtualFiles from the original file name's screenshot node
                successfullyRenamed.forEach(reference -> selectedScreenshotNode.getReferences().remove(reference));
                if (selectedScreenshotNode.getReferences().isEmpty()) {
                    //Since there is no actual file remained for the original screenshot node,
                    // it can be removed from the spec, and the one with the new name will be shown
                    parentSpec.getScreenshots().remove(selectedScreenshotNode);
                }
                //Reorder and update the UI even when just a portion of the screenshots could be renamed
                parentSpec.reorderScreenshotsAlphabeticallyByDisplayName();
                tree.updateUI();
                if (!erroredFilePaths.isEmpty()) {
                    Messages.showWarningDialog(project,
                        TerraBundle.toolWindow("rename.could.not.rename.screenshots") + String.join("\n", erroredFilePaths),
                        TerraBundle.toolWindow("rename.error.during.rename"));
                } else {
                    tree.getSelectionModel().clearSelection(); //Fixes #24
                }
            } else {
                Messages.showWarningDialog(project,
                    TerraBundle.toolWindow("rename.non.writable.files.description"),
                    TerraBundle.toolWindow("rename.non.writable.files.title"));
            }
        }
    }

    /**
     * Shows an input dialog pre-populated with the original filename (also highlighting the filename without the extension
     * for easier editing experience).
     * <p>
     * It checks the input for the following: the new filename is not blank
     * <p>
     * If any of these two conditions is met, the renaming is not allowed.
     * <p>
     * If the provided name doesn't contain the original file extension, this method puts it back into the final new filename.
     *
     * @param originalFileName the original filename
     * @return the new filename, or null if the user Cancelled or closed the input dialog
     */
    @Nullable
    private String askUserForNewFileName(String originalFileName, String extension) {
        String originalFileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String newFileName = Messages.showInputDialog(project,
            TerraBundle.toolWindow("rename.provide.new.name"),
            TerraBundle.toolWindow("rename.screenshots"), null, originalFileName,
            new InputValidator() {
                @Override
                public boolean checkInput(String inputString) {
                    return !inputString.isBlank() &&
                        inputString.endsWith(extension)
                        ? !inputString.substring(0, inputString.lastIndexOf('.')).equals(originalFileNameWithoutExt)
                        : !inputString.equals(originalFileNameWithoutExt);
                }

                @Override
                public boolean canClose(String inputString) {
                    return true;
                }
            },
            new TextRange(0, originalFileName.lastIndexOf(".")));
        //If the user gave a new name, and it doesn't include the original extension of the file, then we put it back
        if (newFileName != null && !newFileName.endsWith("." + extension)) {
            newFileName += "." + extension;
        }
        return newFileName;
    }

    /**
     * Gets whether the argument key event corresponds to this action's shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isRenameScreenshotsShortcutKey(KeyEvent e) {
        return e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_F6;
    }
}
