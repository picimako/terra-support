//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static java.util.stream.Collectors.toList;

import java.beans.PropertyChangeListener;
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
import com.intellij.util.SmartList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * An abstract implementation of {@link FileEditor}.
 * <p>
 * If, for some reason, no image can be display in the editor, a message saying {@code There is no screenshot available to display.}
 * is shown instead. See {@link #getComponent()}.
 *
 * @since 0.1.0
 */
@Getter
public abstract class AbstractScreenshotsPreview implements FileEditor {

    protected final List<ScreenshotDiff> screenshotDiffs = new SmartList<>();
    protected final Project project;

    protected AbstractScreenshotsPreview(@NotNull Project project, @NotNull VirtualFile file, @NotNull String sourceFolderName,
                                         @NotNull Function<VirtualFile, ScreenshotDiff> screenshotToDiffMapper) {
        var wdioFolder = TerraWdioFolders.projectWdioRoot(project);
        //There should never be a case when the wdio root is null here. Since previews can only be initiated
        // from/via screenshots. Having at least one screenshot means that there is a wdio root.
        if (wdioFolder != null) {
            wdioFolder.refresh(false, true);
            var specFolderCollector = TerraResourceManager.getInstance(project).specFolderCollector();
            this.screenshotDiffs.addAll(specFolderCollector.collectSpecFoldersForTypeInside(sourceFolderName, VfsUtil.collectChildrenRecursively(wdioFolder))
                .flatMap(spec -> Arrays.stream(VfsUtil.getChildren(spec))) //individual screenshot files
                .filter(screenshot -> file.getName().equals(screenshot.getName()))
                .map(screenshotToDiffMapper)
                .collect(toList()));
        }
        this.project = project;
    }

    /**
     * Overriding and implementing this method is required. Since 2021.1
     * {@code com.intellij.openapi.fileEditor.impl.IdeDocumentHistoryImpl#createPlaceInfo(FileEditor, FileEditorProvider)}
     * has changed how it retrieves the file associated with the editor.
     * <p>
     * Since then, it is retrieved from {@link FileEditor} instead of {@link com.intellij.openapi.fileEditor.ex.FileEditorManagerEx}.
     * <p>
     * If this method is not implemented, opening any of the Terra screenshot editors will fail with NPE due the default implementation
     * of this method.
     * <p>
     * See the related <a href="https://youtrack.jetbrains.com/issue/IDEA-264044">YouTrack ticket</a> for details.
     */
    @Override
    public @Nullable VirtualFile getFile() {
        //At this point it is guaranteed that there will be at least one file to associate the editor with.
        return screenshotDiffs.get(0).getOriginal();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return screenshotDiffs.isEmpty()
            ? TerraScreenshotsDiffViewContainer.noScreenshotAvailable()
            : new TerraScreenshotsDiffViewContainer(screenshotDiffs, uiContentProvider(), project);
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
    public void dispose() {
        var imageEditorCache = ImageEditorCache.getInstance(project);
        imageEditorCache.disposeEditorFor(getFile());
        for (var screenshotDiff : screenshotDiffs) {
            imageEditorCache.disposeEditorFor(screenshotDiff.getOriginal());
            if (screenshotDiff.getLatest() != null)
                imageEditorCache.disposeEditorFor(screenshotDiff.getLatest());
        }
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
