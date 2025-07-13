//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * An action to replace reference screenshot files with latest screenshots via the Terra wdio tool window.
 * <p>
 * It replaces all available screenshots for the name the user selected in the tool window.
 * <p>
 * NOTE: it might happen that the diff icon of a screenshot node (that it has diff image), and the status of this menu action
 * are not consistent. It can happen in cases when the diff and/or latest folders are modified manually.
 *
 * @since 0.1.0
 */
public class ReplaceReferenceWithLatestAction extends AbstractTerraWdioToolWindowAction {

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
            var selectedScreenshotNode = asScreenshot(tree.getLastSelectedPathComponent());

            final var erroredFilePaths = new SmartList<String>();
            final var referencesToRemove = new HashSet<VirtualFile>();
            final var copiedFromLatests = new HashSet<VirtualFile>();
            final var diffsToRemove = new HashSet<VirtualFile>();
            for (var latestVf : selectedScreenshotNode.getLatests()) {
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
                var parentSpec = (TreeSpecNode) tree.getSelectionPath().getParentPath().getLastPathComponent();
                parentSpec.reorderScreenshotsAlphabeticallyByDisplayName();
                tree.updateUI();
            } else {
                Messages.showWarningDialog(project,
                    TerraBundle.toolWindow("replace.reference.could.not.replace.screenshots") + String.join("\n", erroredFilePaths),
                    TerraBundle.toolWindow("replace.reference.error.during.replacement"));
            }
        }
    }

    /**
     * Checks for the preconditions of this action, disables the action when the selected screenshot has no corresponding
     * image file under the latest folder.
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
     * Gets whether the argument key event corresponds to this action's shortcut key.
     *
     * @param e the key event
     * @return true if the key event is this action's shortcut, false otherwise
     */
    public static boolean isReplaceScreenshotsShortcutKey(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R;
    }
}
