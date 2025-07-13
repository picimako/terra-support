//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
