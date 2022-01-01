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

package com.picimako.terra.wdio.toolwindow.node;

import java.util.List;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.ScreenshotStatisticsProjectService;

/**
 * Represents a screenshot node in the tree displayed in the Terra wido tool window.
 */
public class TreeScreenshotNode extends AbstractTerraWdioTreeNode {

    protected final List<VirtualFile> diffs = new SmartList<>();
    protected final List<VirtualFile> latests = new SmartList<>();
    protected boolean unused = false;

    public TreeScreenshotNode(@NotNull String displayName, Project project) {
        super(displayName, project);
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
     * Gets whether this node has at least one latest image.
     */
    public boolean hasLatest() {
        return !latests.isEmpty();
    }

    /**
     * Gets whether this node has at least one diff image.
     */
    public boolean hasDiff() {
        return !diffs.isEmpty();
    }

    public boolean isUnused() {
        return unused;
    }

    public void setUnused(boolean unused) {
        this.unused = unused;
    }

    @Override
    public String toString() {
        return ScreenshotStatisticsProjectService.getInstance(project).isShowStatistics
            ? displayName + " (" + references.size() + ")"
            : displayName;
    }

    @Override
    public void dispose() {
        super.dispose();
        latests.clear();
        diffs.clear();
    }
}
