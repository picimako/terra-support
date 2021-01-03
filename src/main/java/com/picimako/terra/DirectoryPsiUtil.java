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

package com.picimako.terra;

import java.util.Optional;

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
                .map(subDir -> PsiManager.getInstance(project).findDirectory(subDir))
                .orElse(null)
            : null;
    }

    private DirectoryPsiUtil() {
        //Utility class
    }
}
