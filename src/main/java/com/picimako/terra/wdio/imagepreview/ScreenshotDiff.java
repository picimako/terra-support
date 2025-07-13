//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import com.intellij.openapi.vfs.VirtualFile;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a screenshot diff, meaning either an actual diff image, or a pair of a reference and a latest image.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class ScreenshotDiff {

    /**
     * Can be a reference or diff image. If it is a diff, then {@link #latest} will be null.
     */
    @NotNull
    private final VirtualFile original;
    @Nullable
    private VirtualFile latest;

    public ScreenshotDiff(@NotNull VirtualFile original) {
        this.original = original;
    }

    /**
     * Gets whether this diff object has a latest image set.
     */
    public boolean hasLatest() {
        return latest != null;
    }
}
