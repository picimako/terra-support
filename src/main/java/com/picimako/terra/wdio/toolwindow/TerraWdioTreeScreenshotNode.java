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

package com.picimako.terra.wdio.toolwindow;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a screenshot node in the tree displayed in the Terra wido tool window.
 */
public class TerraWdioTreeScreenshotNode extends AbstractTerraWdioTreeNode {

    protected final List<VirtualFile> diffs = new ArrayList<>();
    protected final List<VirtualFile> latests = new ArrayList<>();

    public TerraWdioTreeScreenshotNode(@NotNull String displayName) {
        super(displayName);
    }

    public void addDiff(VirtualFile virtualFile) {
        diffs.add(virtualFile);
    }

    public List<VirtualFile> getDiffs() {
        return diffs;
    }

    public void addLatest(VirtualFile virtualFile) {
        latests.add(virtualFile);
    }

    public List<VirtualFile> getLatests() {
        return latests;
    }

    /**
     * Gets whether this node has a latest image.
     *
     * @return true if the node has at least one latest image, false otherwise
     */
    public boolean hasLatest() {
        return !latests.isEmpty();
    }

    /**
     * Gets whether this node has a diff image.
     *
     * @return true if the node has at least one diff image, false otherwise
     */
    public boolean hasDiff() {
        return !diffs.isEmpty();
    }

    @Override
    public String toString() {
        return displayName + " (" + references.size() + ")";
    }

    @Override
    public void dispose() {
        super.dispose();
        latests.clear();
        diffs.clear();
    }
}
