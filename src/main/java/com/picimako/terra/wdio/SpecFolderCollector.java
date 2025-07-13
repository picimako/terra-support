//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Collects spec folders based on image type.
 *
 * @param folderType Returns in which image type the argument folder is located: diff, latest, reference.
 */
public record SpecFolderCollector(@Nullable("Only by the no-op collector.") Function<VirtualFile, String> folderType) {

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
    public Stream<VirtualFile> collectSpecFoldersForTypeInside(@NotNull String imageType, @NotNull List<VirtualFile> filesAndFoldersInWdioRoot) {
        return filesAndFoldersInWdioRoot.stream()
            .filter(VirtualFile::isDirectory)
            .filter(dir -> dir.getName().endsWith("-spec"))
            .filter(dir -> imageType.equals(folderType.apply(dir)));
    }
}
