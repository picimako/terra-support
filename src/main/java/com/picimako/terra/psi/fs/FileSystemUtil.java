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

package com.picimako.terra.psi.fs;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods to retrieve file system related information from PSI elements.
 */
public final class FileSystemUtil {

    /**
     * Returns the path of the argument element's containing file in a system dependent format (slashes or backslashes
     * according to the current system).
     * <p>
     * If the containing file doesn't have a canonical path, then the containing file's name is returned (including the file extension).
     */
    @NotNull
    public static String filePathOf(PsiElement element) {
        final String systemIndependentPath = element.getContainingFile().getViewProvider().getVirtualFile().getCanonicalPath();
        return systemIndependentPath != null ? FileUtil.toSystemDependentName(systemIndependentPath) : element.getContainingFile().getName();
    }

    private FileSystemUtil() {
        //Utility class
    }
}
