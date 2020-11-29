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

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.TerraWdioFolders.collectSpecFoldersInside;
import static java.util.stream.Collectors.toList;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Am abstract implementation of {@link FileEditor}.
 * <p>
 * If, for some reason, no image can be display in the editor, a message saying {@code There is no screenshot available to display.}
 * is shown instead. See {@link #getComponent()}.
 */
public abstract class AbstractScreenshotsPreview implements FileEditor {

    protected final List<ScreenshotDiff> screenshotDiffs = new ArrayList<>();
    protected final Project project;

    protected AbstractScreenshotsPreview(@NotNull Project project, @NotNull VirtualFile file, @NotNull String sourceFolderName,
                                         @NotNull Function<VirtualFile, ScreenshotDiff> screenshotToDiffMapper) {
        VirtualFile wdioFolder = TerraWdioFolders.projectWdioRoot(project);
        if (wdioFolder != null) {
            wdioFolder.refresh(false, true);
            this.screenshotDiffs.addAll(collectSpecFoldersInside(sourceFolderName, VfsUtil.collectChildrenRecursively(wdioFolder))
                .flatMap(spec -> Arrays.stream(VfsUtil.getChildren(spec))) //individual screenshot files
                .filter(screenshot -> file.getName().equals(screenshot.getName()))
                .map(screenshotToDiffMapper)
                .collect(toList()));
        }
        this.project = project;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return screenshotDiffs.isEmpty()
            ? TerraScreenshotsDiffViewContainer.noScreenshotAvailable()
            : new TerraScreenshotsDiffViewContainer(screenshotDiffs, uiContentProvider());
    }

    /**
     * Gets the UI content provider implementation associated with this object.
     */
    @NotNull
    protected abstract ScreenshotDiffUIContentProvider uiContentProvider();

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    }
}