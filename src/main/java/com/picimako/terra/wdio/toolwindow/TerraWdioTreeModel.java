//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.TerraWdioFolders.existsAfterRefresh;
import static com.picimako.terra.wdio.TerraWdioFolders.projectWdioRoot;
import static com.picimako.terra.wdio.TerraWdioFolders.specFolderIdentifier;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asSpec;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.isSpec;

import java.util.List;
import java.util.Set;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;

/**
 * A model object for backing the {@link TerraWdioTree} UI component.
 * <p>
 * It maps the original folder structure of Terra wdio files (having {@code tests/wdio} as the base folder).
 * <p>
 * <b>For terra-toolkit:</b>
 * <pre>
 * - tests
 *     - wdio
 *         - __snapshots__
 *              - reference
 *                  - &lt;locale>
 *                      - &lt;browser>_&lt;viewport>
 *                          - &lt;spec file name>
 *                              - &lt;screenshot_1>.png
 *                              - &lt;screenshot_2>.png
 *                              - ...
 *              - &lt;spec-file-name>-spec.js
 *              - &lt;another-spec-file-name>-spec.js
 *              - &lt;nestedfolder>
 *                 - &lt;nested>-spec.js
 *                 - __snapshots__
 *                     - &lt;screenshot folder structure>
 * </pre>
 * <b>For terra-functional-testing</b>
 * <pre>
 * - tests
 *     - wdio
 *         - __snapshots__
 *              - reference
 *                  - &lt;theme>
 *                      - &lt;locale>
 *                          - &lt;browser>_&lt;viewport>
 *                              - &lt;spec file name>
 *                                  - &lt;screenshot_1>.png
 *                                  - &lt;screenshot_2>.png
 *                                  - ...
 *              - &lt;spec-file-name>-spec.js
 *              - &lt;another-spec-file-name>-spec.js
 *              - &lt;nestedfolder>
 *                 - &lt;nested>-spec.js
 *                 - __snapshots__
 *                     - &lt;screenshot folder structure>
 * </pre>
 * It creates a new, accumulated view for the Terra wdio tool window, so that users can handle multiple specs and screenshots with bulk commands:
 * <pre>
 * - Wdio Resources                  <- This is the root node in the tree, which is permanent. It cannot be modified or removed.
 *      - &lt;spec file name>           <- A folder node referencing all spec folders and the corresponding spec file with the same name.
 *          - &lt;screenshot_1>.png     <- A screenshot node referencing all screenshot files with the same name.
 *          - &lt;screenshot_2>.png
 *      - &lt;another spec file name>
 *          - &lt;screenshot_3>.png
 *      - &lt;nestedfolder>/&lt;nested>-spec
 *          - &lt;screenshots>
 * </pre>
 * This model also supports when spec JS files are in single or multiple subfolders within the base test path.
 * They are collected in the same way into this model.
 */
public class TerraWdioTreeModel extends AbstractTerraWdioTreeModel {

    public TerraWdioTreeModel(Project project) {
        super(project);
    }

    @Override
    protected void collectSpecsAndScreenshots(@NotNull List<VirtualFile> filesAndFoldersAnywhereInWdioRoot, Set<VirtualFile> specFiles,
                                              @NotNull String imageType, @NotNull VirtualFileToNodeAdder virtualFileToNodeAdder) {
        String wdioRootPath = projectWdioRoot(project).getPath();
        //This will have duplicate folders by name, but they are different folders
        TerraResourceManager.getInstance(project).specFolderCollector()
            .collectSpecFoldersForTypeInside(imageType, filesAndFoldersAnywhereInWdioRoot)
            .forEach(folder -> {
                var screenshots = VfsUtil.getChildren(folder);
                String folderIdentifier = specFolderIdentifier(folder, project);
                data.getSpecs().stream()
                    .filter(spec -> spec.getDisplayName().equals(folderIdentifier)) //to make sure that the UI tree will contain a single node for a given spec name
                    .findFirst()
                    .ifPresentOrElse(
                        //If the given spec folder (specNode) has already been added
                        specNode -> populateSpecNodeWithFolderAndScreenshots(folder, screenshots, specNode, virtualFileToNodeAdder, imageType),
                        //If a given spec folder hasn't been added
                        () -> doIfSpecFolderHasntBeenAdded(specFiles, imageType, virtualFileToNodeAdder, wdioRootPath, folder, screenshots, folderIdentifier));
            });
    }

    private void doIfSpecFolderHasntBeenAdded(Set<VirtualFile> specFiles, @NotNull String imageType,
                                              @NotNull VirtualFileToNodeAdder virtualFileToNodeAdder, String wdioRootPath,
                                              VirtualFile folder, VirtualFile[] screenshots,
                                              String folderIdentifier) {
        if (existsAfterRefresh(folder)) {
            var specNode = TerraWdioTreeNode.forSpec(folderIdentifier, project);
            populateSpecNodeWithFolderAndScreenshots(folder, screenshots, specNode, virtualFileToNodeAdder, imageType);

            //Adds the spec file that belongs to the spec node. This is necessary for the "Navigate to Usage" screenshot action.
            specFiles.stream()
                //This makes sure that in case of multiple spec files with the same name in different folders, the correct file is selected and added.
                .filter(specFile -> specFile.getPath().substring(0, specFile.getPath().lastIndexOf(".")).equals(wdioRootPath + "/" + folderIdentifier))
                .findFirst()
                .ifPresent(specNode::setSpecFile);

            data.getSpecs().add(specNode);
            Disposer.register(data, specNode);
        }
    }

    // The methods below are responsible for building the actual tree model from the backing model data.

    @Override
    public Object getChild(Object parent, int index) {
        TerraWdioTreeNode child = null;
        if (parent instanceof TreeModelDataRoot) {
            child = data.getSpecs().get(index);
        } else if (isSpec(parent)) {
            child = asSpec(parent).getScreenshot(index);
        }
        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        int count = 0;
        if (parent instanceof TreeModelDataRoot) {
            count = data.getSpecs().size();
        } else if (isSpec(parent)) {
            count = asSpec(parent).screenshotCount();
        }
        return count;
    }

    @Override
    public boolean isLeaf(Object node) {
        boolean isLeaf = data.getSpecs().isEmpty();
        if (!(node instanceof TreeModelDataRoot)) {
            int index = data.getSpecs().indexOf(node);
            isLeaf = !isSpec(node) || (index != -1 && data.getSpecs().get(index).screenshotCount() == 0);
        }
        return isLeaf;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int indexOfChild = 0;
        if (parent != null && child != null) {
            if (parent instanceof TreeModelDataRoot) {
                indexOfChild = data.getSpecs().indexOf(child);
            } else if (isSpec(parent)) {
                indexOfChild = asSpec(parent).getScreenshots().indexOf(child);
            }
        } else {
            indexOfChild = -1;
        }
        return indexOfChild;
    }
}
