//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow.node;

import java.util.List;
import java.util.Objects;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Node type for the elements of {@link TerraWdioTree}.
 * <p>
 * There are three types of nodes at the moment:
 * <ul>
 *     <li>root: there is a single root that is permanent</li>
 *     <li>specs: one or more nodes for the spec files</li>
 *     <li>screenshots: one or more nodes for the screenshots under each spec file</li>
 * </ul>
 * <p>
 * Each node stores the name of the file or folder it references, and also the actual files or folders as {@link VirtualFile} instances
 * (called references in this context), so that the file system resources can be reached and manipulated easier.
 */
public abstract class AbstractTerraWdioTreeNode implements TerraWdioTreeNode {
    @Getter
    protected final List<VirtualFile> references = new SmartList<>();
    protected final String displayName;
    protected final Project project;

    protected AbstractTerraWdioTreeNode(@NotNull String displayName, Project project) {
        this.displayName = displayName;
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Adds a {@link VirtualFile} reference to this node.
     *
     * @param virtualFile the reference to add
     */
    public void addReference(VirtualFile virtualFile) {
        references.add(virtualFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTerraWdioTreeNode that = (AbstractTerraWdioTreeNode) o;
        return Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }

    @Override
    public void dispose() {
        references.clear();
    }
}
