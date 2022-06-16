//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.settings;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Container object for a single wdio root path value.
 *
 * @see WdioRootPathsTableModelCreator
 */
public final class RootPath {

    public static final RootPath EMPTY = new RootPath();
    private String path;

    public RootPath() {
        this("");
    }

    public RootPath(@NotNull String path) {
        this.path = path;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RootPath rootPath = (RootPath) o;
        return Objects.equals(path, rootPath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
