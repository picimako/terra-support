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

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a spec file/folder in the tree displayed in the Terra wdio tool window.
 * <p>
 * Each spec node knows about, and stores the screenshots (as nodes) that it contains.
 * <p>
 * The number of underlying screenshots may be zero, because there might be cases when all tests within the corresponding
 * spec file are implemented without taking any screenshots.
 */
public class TerraWdioTreeSpecNode extends AbstractTerraWdioTreeNode {

    private final List<TerraWdioTreeScreenshotNode> screenshots = new ArrayList<>();
    private final Project project;
    private VirtualFile specFile;

    public TerraWdioTreeSpecNode(@NotNull String displayName, Project project) {
        super(displayName);
        this.project = project;
    }

    /**
     * Adds a screenshot node to this spec node.
     *
     * @param screenshot the node to add
     */
    public void addScreenshot(@NotNull TerraWdioTreeScreenshotNode screenshot) {
        this.screenshots.add(screenshot);
    }

    /**
     * Gets all screenshot nodes stored in this node.
     *
     * @return the underlying screenshot nodes
     */
    public List<TerraWdioTreeScreenshotNode> getScreenshots() {
        return screenshots;
    }

    /**
     * Returns the index-th screenshot from this node.
     *
     * @param index the 0-based index
     * @return the nth screenshot node
     */
    public TerraWdioTreeScreenshotNode getScreenshot(int index) {
        return screenshots.get(index);
    }

    @Nullable
    public VirtualFile getSpecFile() {
        return specFile;
    }

    public void setSpecFile(VirtualFile specFile) {
        this.specFile = specFile;
    }

    /**
     * Searches for an underlying screenshot node by the name provided as argument, and returns it as an Optional.
     * <p>
     * If there is no such node found, it returns an empty Optional.
     *
     * @param name the name to search by, never null
     * @return the node wrapped in Optional, otherwise empty Optional
     */
    public Optional<TerraWdioTreeScreenshotNode> findScreenshotNodeByName(@NotNull String name) {
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
     *
     * @return the number of screenshot nodes
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerraWdioTreeSpecNode that = (TerraWdioTreeSpecNode) o;
        return Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
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
        screenshots.clear();
    }
}
