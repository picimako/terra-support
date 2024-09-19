//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra;

import java.util.Optional;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

/**
 * Provides functionality to find directories in a project.
 *
 * @since 0.3.0
 */
public final class DirectoryPsiUtil {

    /**
     * Gets the PSI Directory corresponding to the {@code <project root>/<directoryPath>} directory.
     *
     * @param project       the project to find the directory in
     * @param directoryPath the relative path of the directory to find in the project
     *                      It allows null values, to make it easier handling non-existent directories.
     * @return the PSI element corresponding to the provided directory name, or null if there is no such directory
     */
    @Nullable
    public static PsiDirectory findDirectory(Project project, @Nullable String directoryPath) {
        return directoryPath != null ?
            Optional.ofNullable(ProjectUtil.guessProjectDir(project))
                .filter(VirtualFile::exists)
                .map(projectDir -> projectDir.findFileByRelativePath(directoryPath))
                .map(subDir -> ReadAction.compute(() -> PsiManager.getInstance(project).findDirectory(subDir)))
                .orElse(null)
            : null;
    }

    private DirectoryPsiUtil() {
        //Utility class
    }
}
