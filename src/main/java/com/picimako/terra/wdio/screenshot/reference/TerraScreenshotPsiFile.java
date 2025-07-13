//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.reference;

import javax.swing.*;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import org.intellij.images.fileTypes.impl.ImageFileType;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.ScreenshotContextParser;

/**
 * A wrapper class for a binary file, specifically for the screenshot images, so that their representation in the
 * suggestion list is customized.
 * <p>
 * It will show the name of the image (with their extensions), and the context information (locale, browser, viewport)
 * as the location string. For example: {@code "some_image[default].png (in en/chrome/huge)"} without the double-quotes.
 */
final class TerraScreenshotPsiFile extends PsiBinaryFileImpl {

    private final ScreenshotContextParser contextParser;
    private final PsiFile originalElement;

    TerraScreenshotPsiFile(PsiFile originalElement) {
        super((PsiManagerImpl) originalElement.getManager(), originalElement.getViewProvider());
        contextParser = TerraResourceManager.getInstance(getProject()).screenshotContextParser("/");
        this.originalElement = originalElement;
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return originalElement.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return contextParser.parse(originalElement.getVirtualFile().getPath());
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ImageFileType.INSTANCE.getIcon();
            }
        };
    }
}
