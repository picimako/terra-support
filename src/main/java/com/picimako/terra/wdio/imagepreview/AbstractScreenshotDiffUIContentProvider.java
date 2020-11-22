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

import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract UI content provider implementation for providing common methods for UI component creation.
 */
public abstract class AbstractScreenshotDiffUIContentProvider implements ScreenshotDiffUIContentProvider {

    private final Project project;

    public AbstractScreenshotDiffUIContentProvider(@NotNull Project project) {
        this.project = project;
    }

    protected JPanel createImageEditorFor(@NotNull VirtualFile file) {
        return new ImageEditorImpl(project, file).getComponent();
    }
}
