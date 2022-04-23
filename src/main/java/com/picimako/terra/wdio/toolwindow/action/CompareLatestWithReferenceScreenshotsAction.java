//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.action;

import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isScreenshot;

import java.awt.event.KeyEvent;
import java.util.List;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.imagepreview.ReferenceToLatestScreenshotsPreview;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * An action to open a special Terra screenshot view for comparing reference and latest screenshots, via the Terra wdio tool window.
 *
 * @since 0.1.0
 */
public class CompareLatestWithReferenceScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    public CompareLatestWithReferenceScreenshotsAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("compare.latests.with.references"), project);
    }

    /**
     * Opens a {@link com.picimako.terra.wdio.imagepreview.ReferenceToLatestScreenshotsPreview} for the
     * selected screenshot.
     * <p>
     * To determine the name of the screenshot for which a new editor will be opened, it uses the first screenshot
     * stored as latest for the screenshot node this action was invoked on.
     * <p>
     * It doesn't matter which latest image is opened because the reference/latest preview will display all of them,
     * it is only the name of the image that is required.
     * <p>
     * If an editor for the file is already open, it puts that editor in focus.
     *
     * @param tree    the wdio tree where this action is invoked on
     * @param project the project
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (project!= null && tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            List<VirtualFile> latests = asScreenshot(tree.getLastSelectedPathComponent()).getLatests();
            if (!latests.isEmpty()) {
                VirtualFile fileToOpen = latests.get(0);
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openFile(fileToOpen, true, fileEditorManager.isFileOpen(fileToOpen));
                fileEditorManager.setSelectedEditor(fileToOpen, ReferenceToLatestScreenshotsPreview.EDITOR_TYPE_ID);
            }
        }
    }

    @Override
    protected boolean meetsPreconditions(TerraWdioTree tree) {
        return asScreenshot(tree.getLastSelectedPathComponent()).hasLatest();
    }

    /**
     * Checks whether the argument key event's keycode corresponds to the ENTER button, essentially checking whether
     * the user hit ENTER.
     */
    public static boolean isCompareLatestsWithReferencesShortcutKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }
}
