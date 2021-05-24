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
import java.util.Objects;

import com.intellij.openapi.project.Project;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.toolwindow.ScreenshotStatisticsProjectService;

/**
 * Each node in the tree is identified by its name: in case of spec nodes, by the spec file's name without the file extension,
 * in case of screenshots, by the screenshot's whole name, including the extension.
 * <p>
 * Both spec nodes and screenshot nodes store the Virtual Files that are available in the project with the same name,
 * so that bulk actions like delete, rename, etc. can be carried out by interacting with a single node on the UI.
 * <p>
 * This node also displays the basic statistics about the overall, distinct number of specs and screenshot files in the project.
 * Both the spec and screenshot values handle singular and plural cases based on the count values.
 */
public class TreeModelDataRoot extends AbstractTerraWdioTreeNode {

    private final List<TreeSpecNode> specs = new SmartList<>();

    public TreeModelDataRoot(@NotNull String displayName, Project project) {
        super(displayName, project);
    }

    public List<TreeSpecNode> getSpecs() {
        return specs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeModelDataRoot that = (TreeModelDataRoot) o;
        return Objects.equals(specs, that.specs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specs);
    }

    @Override
    public String toString() {
        return ScreenshotStatisticsProjectService.getInstance(project).isShowStatistics
            ? TerraBundle.toolWindow("root.node.name.with.stat", specs.size(), screenshotCount())
            : displayName;
    }

    private int screenshotCount() {
        return specs.stream()
            .flatMap(spec -> spec.getScreenshots().stream())
            .mapToInt(screenshot -> screenshot.getReferences().size())
            .sum();
    }

    @Override
    public void dispose() {
        super.dispose();
        specs.forEach(TreeSpecNode::dispose);
        specs.clear();
    }
}
