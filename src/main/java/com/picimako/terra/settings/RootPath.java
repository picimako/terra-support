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

package com.picimako.terra.settings;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Container object for a single wdio root path value.
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
