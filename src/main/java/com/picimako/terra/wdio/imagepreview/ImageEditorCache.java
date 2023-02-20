//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.images.editor.impl.ImageEditorImpl;

/**
 * Caches {@link ImageEditorImpl} instances per image/screenshot file to be able to provide better disposal
 * logic for them.
 *
 * @since 1.3.0
 */
@Service(Service.Level.PROJECT)
public final class ImageEditorCache implements Disposable {

    private final Map<VirtualFile, ImageEditorImpl> imageEditorCache;

    public ImageEditorCache(Project project) {
        this.imageEditorCache = new ConcurrentHashMap<>();
    }

    /**
     * Caches the provided image editor for the given image file.
     *
     * @param file        the image file
     * @param imageEditor the image editor
     */
    public void cacheImageEditorFor(VirtualFile file, ImageEditorImpl imageEditor) {
        imageEditorCache.put(file, imageEditor);
    }

    /**
     * Disposes the editor for the given image file.
     */
    public void disposeEditorFor(VirtualFile file) {
        var imageEditor = imageEditorCache.get(file);
        if (imageEditor != null) {
            ((Disposable) imageEditor.getComponent()).dispose();
            imageEditor.dispose();
            imageEditorCache.remove(file);
        }
    }

    @Override
    public void dispose() {
        imageEditorCache.clear();
    }

    public static ImageEditorCache getInstance(Project project) {
        return project.getService(ImageEditorCache.class);
    }
}
