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

import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.imagepreview.DiffScreenshotsPreview;
import com.picimako.terra.wdio.toolwindow.TerraWdioTree;

/**
 * An action to open a special Terra screenshot view for displaying diff screenshots, via the Terra wdio tool window.
 *
 * @since 0.1.0
 */
public class ShowDiffScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    public ShowDiffScreenshotsAction(@NotNull Project project) {
        super(TerraBundle.toolWindow("show.diff.title"), project);
        setShortcutSet(CommonShortcuts.getDiff());
    }

    /**
     * Opens a {@link com.picimako.terra.wdio.imagepreview.DiffScreenshotsPreview} for the selected screenshot.
     * <p>
     * To determine the name of the screenshot for which a new editor will be opened, it uses the first screenshot
     * stored as diff for the screenshot node this action was invoked on.
     * <p>
     * It doesn't matter which diff image is opened because the diff preview will display all of them,
     * it is only the name of the image that is required.
     * <p>
     * If an editor for the file is already open, it puts that editor in focus.
     *
     * @param tree    the wdio tree where this action is invoked on
     * @param project the project
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (project != null && tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            VirtualFile fileToOpen = asScreenshot(tree.getLastSelectedPathComponent()).getDiffs().get(0);
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(fileToOpen, true, fileEditorManager.isFileOpen(fileToOpen));
            fileEditorManager.setSelectedEditor(fileToOpen, DiffScreenshotsPreview.EDITOR_TYPE_ID);
        }
    }

    /**
     * Checks for the preconditions of this action, specifically that the selected screenshot has diff image.
     */
    @Override
    protected boolean meetsPreconditions(TerraWdioTree tree) {
        return asScreenshot(tree.getLastSelectedPathComponent()).hasDiff();
    }

    /**
     * Checks whether the argument key event's keycode corresponds to the Ctrl+D button combination, essentially checking whether
     * the user hit Ctrl+D.
     */
    public static boolean isShowDiffsShortcutKey(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D;
    }
}
