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
     *
     * @return true if there is a latest image, false otherwise
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
