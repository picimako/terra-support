//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.imagepreview;

import java.util.Objects;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a screenshot diff, meaning either an actual diff image, or a pair of a reference and a latest image.
 */
public final class ScreenshotDiff {

    private final VirtualFile original; //may be reference or diff
    private VirtualFile latest;

    public ScreenshotDiff(@NotNull VirtualFile original) {
        this.original = original;
    }

    public ScreenshotDiff(@NotNull VirtualFile original, @Nullable VirtualFile latest) {
        this.original = original;
        this.latest = latest;
    }

    public VirtualFile getOriginal() {
        return original;
    }

    @Nullable
    public VirtualFile getLatest() {
        return latest;
    }

    /**
     * Gets whether this diff object has a latest image set.
     */
    public boolean hasLatest() {
        return latest != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenshotDiff that = (ScreenshotDiff) o;
        return Objects.equals(original, that.original) &&
            Objects.equals(latest, that.latest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original, latest);
    }
}
