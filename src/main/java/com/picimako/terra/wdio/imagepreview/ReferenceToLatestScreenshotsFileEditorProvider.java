//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import static com.picimako.terra.wdio.TerraWdioFolders.isLatestScreenshot;
import static com.picimako.terra.wdio.TerraWdioFolders.isReferenceScreenshot;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.images.fileTypes.impl.ImageFileType;
import org.jetbrains.annotations.NotNull;

/**
 * A custom {@link FileEditorProvider} for adding a custom Terra reference / latest screenshots preview editor in
 * addition to the default image editor provided by the IntelliJ platform.
 * <p>
 * The editor is displayed only when the file being opened is an image, and it is located within either one of the {@code reference}
 * or {@code latest} folders among the Terra wdio resources.
 * <p>
 * Hierarchy:
 * <pre>
 * - {@link ReferenceToLatestScreenshotsFileEditorProvider}
 *   - {@link ReferenceToLatestScreenshotsPreview}
 *     - {@link ReferenceToLatestScreenshotsUIProvider}
 * </pre>
 *
 * @since 0.1.0
 */
public class ReferenceToLatestScreenshotsFileEditorProvider implements FileEditorProvider {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return FileTypeRegistry.getInstance().isFileOfType(file, ImageFileType.INSTANCE)
            && (isLatestScreenshot(file, project) || isReferenceScreenshot(file, project));
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new ReferenceToLatestScreenshotsPreview(project, file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return ReferenceToLatestScreenshotsPreview.EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
