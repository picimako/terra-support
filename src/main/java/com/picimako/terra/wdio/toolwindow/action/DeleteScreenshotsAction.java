//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.intellij.openapi.ui.Messages.YES;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.settings.TerraApplicationState;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * An action to delete screenshot files via the Terra wdio tool window.
 * <p>
 * It deletes all available screenshots for the name the user selected in the tool window.
 *
 * @since 0.1.0
 */
public class DeleteScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    /**
     * Creates a DeleteScreenshotsAction instance.
     * <p>
     * Also registers the common Delete shortcut key for this action.
     *
     * @param project the project
     */
    public DeleteScreenshotsAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("delete.screenshots"), project);
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
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent()) && isUserSureToDeleteTheScreenshots()) {
            var selectedScreenshotNode = asScreenshot(tree.getLastSelectedPathComponent());
            final var erroredFilePaths = new ArrayList<String>();
            final var deletedScreenshotReferences = new ArrayList<VirtualFile>();

            for (var reference : selectedScreenshotNode.getReferences()) {
                FileDeletionHandler.checkExistenceAndHandleDeletion(reference, this,
                    () -> deletedScreenshotReferences.add(reference),
                    () -> erroredFilePaths.add(reference.getPath()));
            }
            deletedScreenshotReferences.forEach(reference -> selectedScreenshotNode.getReferences().remove(reference));

            //Delete the screenshot node from the tree model, and update the UI,
            //so that the changes are reflected in the tool window, otherwise show message dialog that deletion was not successful.
            if (erroredFilePaths.isEmpty()) {
                var parentSpec = tree.getParentSpecOfSelected();
                parentSpec.getScreenshots().remove(selectedScreenshotNode);

                //If there is no screenshot node left under the parent spec node after the deletion, then remove the spec node as well. Fixes #19.
                if (parentSpec.screenshotCount() == 0) removeSpecNode(parentSpec, tree);
                else tree.updateUI();
            } else {
                Messages.showWarningDialog(project,
                    TerraBundle.toolWindow("delete.could.not.delete.screenshots") + String.join("\n", erroredFilePaths),
                    TerraBundle.toolWindow("delete.error.during.deletion"));
            }
        }
    }

    /**
     * Removes the provided spec node from the tree.
     * <p>
     * Since with a simple {@link javax.swing.JTree#updateUI()} call, after the removal, the tree becomes collapsed,
     * this method also restores the expansion state of the nodes in the tree from before the removal.
     * <p>
     * Although the saved state contains the not yet removed spec node as well, it doesn't affect the restoration.
     *
     * @param parentSpec the spec node to remove
     * @param tree       the tree to remove from
     */
    private void removeSpecNode(TreeSpecNode parentSpec, TerraWdioTree tree) {
        //Save the expansion state of nodes from before removing the spec node
        var expandedNodes = tree.getAllExpandedNodes();
        tree.getRoot().getSpecs().remove(parentSpec);
        tree.updateUI();
        tree.restoreExpansionStateFrom(expandedNodes);
    }

    private boolean isUserSureToDeleteTheScreenshots() {
        return !TerraApplicationState.getInstance().showConfirmationBeforeScreenshotDeletion
            || Messages.showYesNoDialog(project,
            TerraBundle.toolWindow("delete.are.you.sure"),
            TerraBundle.toolWindow("delete.screenshots"),
            Messages.getQuestionIcon()) == YES;
    }

    /**
     * Gets whether the argument key event corresponds to this action's shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isDeleteScreenshotsShortcutKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DELETE;
    }
}
