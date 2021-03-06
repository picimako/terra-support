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

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.TerraWdioFolders.isInWdioFiles;

import java.util.List;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.action.DeleteScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.RenameScreenshotsAction;
import com.picimako.terra.wdio.toolwindow.action.ReplaceReferenceWithLatestAction;

/**
 * Terra wdio specific implementation of {@link BulkFileListener}.
 * <p>
 * It listens to file system events of the current project, and handles file events, and in case a particular event
 * is initiated outside the Terra wdio tool window (and its actions), then the tool window's tree model is rebuilt.
 * <p>
 * This distinction of which events are handled based on their requestors is necessary, because each action registered to
 * the tool window handles these actions and the UI refresh by themselves, and doesn't have to rely on file system event listening.
 * <p>
 * Also, rebuilding the tree model is tied to precondition that the event should have happened within the test wdio folders.
 * <p>
 * See <a href="https://jetbrains.org/intellij/sdk/docs/basics/virtual_file_system.html">Intellij Dev Guide: Virtual File System</a>
 * for virtual file system related information.
 * <p>
 * See {@link TerraWdioScreenshotsPanel#TerraWdioScreenshotsPanel(Project)} for subscribing to the related topic.
 * <p>
 * TODO:
 * <b>Possible improvement option:</b>
 * <ul>
 *     <li>Update the tree only when the tool window is open.</li>
 *     <li>Additionally, update the tree only when the tool window is being opened. This of course needs handling of what if the tool
 *      window doesn't get closed. In that case it may need to be closed and opened manually, which is not ideal.</li>
 * </ul>
 *
 * @since 0.1.0
 */
public class TerraWdioExternalFileAndFolderChangeListener implements BulkFileListener {

    private final TerraWdioTree tree;
    private final Project project;

    public TerraWdioExternalFileAndFolderChangeListener(TerraWdioTree tree, Project project) {
        this.tree = tree;
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        if (events.stream().anyMatch(this::isRequestedOutsideOfMenuActionsAndInsideWdioFolder)) {
            ((TerraWdioTreeModel) tree.getModel()).buildTree();
            //Updating the UI is necessary because in case of e.g. a Git bulk rollback/revert of files,
            // the tool window got stuck, and display zeros for all stats, until the tool window was closed and reopened
            tree.updateUI();
        }
    }

    private boolean isRequestedOutsideOfMenuActionsAndInsideWdioFolder(VFileEvent event) {
        Object requestor = event.getRequestor();
        return !(event instanceof VFileContentChangeEvent)
            && !(requestor instanceof DeleteScreenshotsAction)
            && !(requestor instanceof RenameScreenshotsAction)
            && !(requestor instanceof ReplaceReferenceWithLatestAction)
            && isInWdioFiles(event.getFile(), project);
    }
}
