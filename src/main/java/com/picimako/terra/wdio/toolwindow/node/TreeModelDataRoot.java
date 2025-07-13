//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.node;

import java.util.List;

import com.intellij.openapi.project.Project;
import com.intellij.util.SmartList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@EqualsAndHashCode(callSuper = true)
public class TreeModelDataRoot extends AbstractTerraWdioTreeNode {

    @Getter
    private final List<TreeSpecNode> specs = new SmartList<>();

    public TreeModelDataRoot(@NotNull String displayName, Project project) {
        super(displayName, project);
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
