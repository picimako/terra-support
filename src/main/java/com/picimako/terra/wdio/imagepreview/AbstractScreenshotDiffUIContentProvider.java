//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract UI content provider implementation for providing common methods for UI component creation.
 */
public abstract class AbstractScreenshotDiffUIContentProvider implements ScreenshotDiffUIContentProvider {

    private final Project project;

    protected AbstractScreenshotDiffUIContentProvider(@NotNull Project project) {
        this.project = project;
    }

    protected JPanel createImageEditorFor(@NotNull VirtualFile file) {
        var imageEditor = new ImageEditorImpl(project, file);
        Disposer.register(ImageEditorCache.getInstance(project), imageEditor);
        ImageEditorCache.getInstance(project).cacheImageEditorFor(file, imageEditor);
        return imageEditor.getComponent();
    }
}
