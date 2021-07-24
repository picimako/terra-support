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

package com.picimako.terra.wdio.screenshot.reference;

import javax.swing.*;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import icons.ImagesIcons;
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
                return ImagesIcons.ImagesFileType;
            }
        };
    }
}
