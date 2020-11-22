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

    protected final List<VirtualFile> references = new ArrayList<>();
    protected final String displayName;

    public AbstractTerraWdioTreeNode(@NotNull String displayName) {
        this.displayName = displayName;
    }

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

    public List<VirtualFile> getReferences() {
        return references;
    }

    @Override
    public void dispose() {
        references.clear();
    }
}
