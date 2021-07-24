/*
 * Copyright 2021 Tamás Balog
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

package com.picimako.terra.wdio;

import java.util.List;
import java.util.stream.Stream;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Collects spec folders based on image type.
 */
@FunctionalInterface
public interface SpecFolderCollector {

    /**
     * Retrieves the folder type based on the {@code [type]/locale/browser_viewport/spec} folder structure.
     */
    SpecFolderCollector TERRA_TOOLKIT_SPEC_COLLECTOR = dir -> dir.getParent().getParent().getParent().getName();
    /**
     * Retrieves the folder type based on the {@code [type]/theme/locale/browser_viewport/spec} folder structure.
     */
    SpecFolderCollector TERRA_FUNCTIONAL_TESTING_SPEC_COLLECTOR = dir -> dir.getParent().getParent().getParent().getParent().getName();

    /**
     * Collect spec folders from within the provided set of files and folders (effectively everything from) the wdio
     * test root, filtered by the provided {@code imageType} folder (diff, latest or reference),
     * and return it as a {@link Stream} for further manipulation.
     *
     * @param imageType                 the image type folder to collect the spec folders from
     * @param filesAndFoldersInWdioRoot the list of files and folders inside the wdio test root
     * @return the stream of matching spec folders
     */
    @NotNull
    default Stream<VirtualFile> collectSpecFoldersForTypeInside(@NotNull String imageType, @NotNull List<VirtualFile> filesAndFoldersInWdioRoot) {
        return filesAndFoldersInWdioRoot.stream()
            .filter(VirtualFile::isDirectory)
            .filter(dir -> dir.getName().endsWith("-spec"))
            .filter(dir -> imageType.equals(getFolderType(dir)));
    }

    /**
     * Returns in which image type the argument folder is located: diff, latest, reference.
     */
    String getFolderType(VirtualFile dir);
}
