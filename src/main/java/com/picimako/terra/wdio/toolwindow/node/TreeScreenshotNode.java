//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.node;

import java.util.List;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.toolwindow.ScreenshotStatisticsProjectService;

/**
 * Represents a screenshot node in the tree displayed in the Terra wido tool window.
 */
@Getter
public class TreeScreenshotNode extends AbstractTerraWdioTreeNode {

    protected final List<VirtualFile> diffs = new SmartList<>();
    protected final List<VirtualFile> latests = new SmartList<>();
    @Setter
    protected boolean unused = false;

    public TreeScreenshotNode(@NotNull String displayName, Project project) {
        super(displayName, project);
    }

    public void addDiff(VirtualFile virtualFile) {
        diffs.add(virtualFile);
    }

    public void addLatest(VirtualFile virtualFile) {
        latests.add(virtualFile);
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
