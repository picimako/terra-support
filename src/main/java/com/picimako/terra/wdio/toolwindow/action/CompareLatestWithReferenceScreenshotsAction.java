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

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.imagepreview.ReferenceToLatestScreenshotsPreview;
import com.picimako.terra.wdio.toolwindow.TerraWdioTree;

/**
 * An action to open a special Terra screenshot view for comparing reference and latest screenshots, via the Terra wdio tool window.
 *
 * @since 0.1.0
 */
public class CompareLatestWithReferenceScreenshotsAction extends AbstractTerraWdioToolWindowAction {

    private static final String COMPARE_LATESTS_WITH_REFERENCES_TITLE = "Compare Latests With References";

    public CompareLatestWithReferenceScreenshotsAction(@NotNull Project project) {
        super(COMPARE_LATESTS_WITH_REFERENCES_TITLE, project);
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
     * If an editor for the file is already open, it focuses that editor.
     *
     * @param tree    the wdio tree where this action is invoked on
     * @param project the project
     */
    @Override
    public void performAction(TerraWdioTree tree, @Nullable Project project) {
        if (tree != null && isScreenshot(tree.getLastSelectedPathComponent())) {
            VirtualFile fileToOpen = asScreenshot(tree.getLastSelectedPathComponent()).getLatests().get(0);
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(fileToOpen, true, fileEditorManager.isFileOpen(fileToOpen));
            fileEditorManager.setSelectedEditor(fileToOpen, ReferenceToLatestScreenshotsPreview.EDITOR_TYPE_ID);
        }
    }

    @Override
    protected boolean meetsPreconditions(TerraWdioTree tree) {
        return asScreenshot(tree.getLastSelectedPathComponent()).hasLatest();
    }

    /**
     * Checks whether the argument keyevent's keycode corresponds to the ENTER button, essentially checking whether
     * the user hit ENTER.
     */
    public static boolean isCompareLatestsWithReferencesShortcutKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }
}
