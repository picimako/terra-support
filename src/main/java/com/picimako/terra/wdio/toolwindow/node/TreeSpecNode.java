//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.node;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Optional;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.toolwindow.ScreenshotStatisticsProjectService;

/**
 * Represents a spec file/folder in the tree displayed in the Terra wdio tool window.
 * <p>
 * Each spec node knows about, and stores the screenshots (as nodes) that it contains.
 * <p>
 * The number of underlying screenshots may be zero, because there might be cases when all tests within the corresponding
 * spec file are implemented without taking any screenshots.
 */
@Getter
public class TreeSpecNode extends AbstractTerraWdioTreeNode {

    //Gets all screenshot nodes stored in this spec node.
    private final List<TreeScreenshotNode> screenshots = new SmartList<>();
    @Nullable
    @Setter
    private VirtualFile specFile;

    public TreeSpecNode(@NotNull String displayName, Project project) {
        super(displayName, project);
    }

    /**
     * Adds a screenshot node to this spec node.
     *
     * @param screenshot the node to add
     */
    public void addScreenshot(@NotNull TreeScreenshotNode screenshot) {
        this.screenshots.add(screenshot);
    }

    /**
     * Returns the index-th screenshot from this node.
     *
     * @param index the 0-based index
     * @return the nth screenshot node
     */
    public TreeScreenshotNode getScreenshot(int index) {
        return screenshots.get(index);
    }

    /**
     * Searches for an underlying screenshot node by the name provided as argument, and returns it as an Optional.
     * <p>
     * If there is no such node found, it returns an empty Optional.
     *
     * @param name the name to search by, never null
     * @return the node wrapped in Optional, otherwise empty Optional
     */
    public Optional<TreeScreenshotNode> findScreenshotNodeByName(@NotNull String name) {
        return screenshots.stream()
            .filter(node -> name.equals(node.getDisplayName()))
            .findFirst();
    }

    /**
     * Gets whether this node has a screenshot node matching the argument display name.
     *
     * @param name the name to search by, never null
     * @return true if there is a screenshot node found, otherwise false
     */
    public boolean hasScreenshotNodeForName(@NotNull String name) {
        return findScreenshotNodeByName(name).isPresent();
    }

    /**
     * Returns the number of underlying screenshot nodes.
     */
    public int screenshotCount() {
        return screenshots.size();
    }

    /**
     * Reorder the underlying screenshots alphabetically using their displayName as the basis of comparison.
     */
    public void reorderScreenshotsAlphabeticallyByDisplayName() {
        screenshots.sort(comparing(AbstractTerraWdioTreeNode::getDisplayName));
    }

    @Override
    public String toString() {
        return ScreenshotStatisticsProjectService.getInstance(project).isShowStatistics
            ? screenshots.isEmpty() ? displayName : displayName + " (" + screenshots.size() + ")"
            : displayName;
    }

    @Override
    public void dispose() {
        super.dispose();
        screenshots.forEach(TreeScreenshotNode::dispose);
        screenshots.clear();
    }
}
