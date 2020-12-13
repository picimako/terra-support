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
