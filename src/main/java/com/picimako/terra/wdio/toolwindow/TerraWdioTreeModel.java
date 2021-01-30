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

import static com.picimako.terra.wdio.TerraWdioFolders.collectSpecFiles;
import static com.picimako.terra.wdio.TerraWdioFolders.collectSpecFoldersInside;
import static com.picimako.terra.wdio.TerraWdioFolders.projectWdioRoot;
import static com.picimako.terra.wdio.TerraWdioFolders.specFolderIdentifier;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asScreenshot;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.asSpec;
import static com.picimako.terra.wdio.toolwindow.TerraWdioTreeNode.isSpec;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraWdioFolders;

/**ew
 * A model object for backing the {@link TerraWdioTree} UI component.
 * <p>
 * It maps the original folder structure of Terra wdio files, like this (having {@code tests/wdio} as the base folder):
 * <pre>
 * - tests
 *     - wdio
 *         - __snapshots__
 *              - diff
 *                  - &lt;locale>
 *                      - &lt;browser>_&lt;viewport>
 *                          - &lt;spec file name>
 *                              - &lt;screenshot_1>.png
 *                          - &lt;another spec file name>
 *                              - &lt;screenshot_3>.png
 *              - latest
 *                  - &lt;locale>
 *                      - &lt;browser>_&lt;viewport>
 *                          - &lt;spec file name>
 *                              - &lt;screenshot_1>.png
 *              - reference
 *                  - &lt;locale>
 *                      - &lt;browser>_&lt;viewport>
 *                          - &lt;spec file name>
 *                              - &lt;screenshot_1>.png
 *                              - &lt;screenshot_2>.png
 *                              - ...
 *              - reports
 *                  - result-&lt;browser>-&lt;application-name>.json
 *              - &lt;spec-file-name>-spec.js
 *              - &lt;another-spec-file-name>-spec.js
 *              - &lt;nestedfolder>
 *                 - &lt;nested>-spec.js
 *                 - __snapshots__
 *                     - &lt;screenshot folder structure>
 * </pre>
 * and creates a new, accumulated view for the Terra wdio tool window, so that users can handle multiple specs and screenshots with bulk commands:
 * <pre>
 * - Wdio Resources                  <- This is the root node in the tree, which is permanent. It cannot be modified or removed.
 *      - &lt;spec file name>           <- A folder node referencing all spec folders and the related spec file with the same name.
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
public class TerraWdioTreeModel implements TreeModel {

    private final Project project;
    private TerraWdioTreeModelDataRoot data;
    private Disposable rootDisposable;

    public TerraWdioTreeModel(Project project) {
        this.project = project;
        buildTree();
    }

    /**
     * Builds the contents of the tree model that is shown in the tool window.
     * <p>
     * <b>Disposer logic</b>
     * <p>
     * Clears all virtual files and screenshot nodes saved in the model.
     * <p>
     * Since changes can and do occur in the project, that are not initiated from the Terra wdio tool window, these external
     * changes, depending on their nature, might have to be reflected on the tool window UI, and the tree model behind it.
     * <p>
     * The current approach is to rebuild the tree model within the same root node object, instead of finding the proper object
     * in the model, corresponding to the actual change in the file system (rename, delete, create, etc.), and modify the same
     * model to reflect the change.
     * <p>
     * This approach also means that with each file system change/event, and model rebuild, there are many objects left behind
     * that will require garbage collection short and long-term, but with each such event, the number of such objects may
     * rise significantly.
     * <p>
     * To help mitigate this problem, and signal to the garbage collector that these objects can be cleaned up, each collection
     * within the model and its nodes are cleared (set to null).
     * <p>
     * The current approach is simpler, requires less code and complexity to maintain, compared to the other approach.
     * <p>
     * In case of any memory leak, or if significant memory consumption is experienced by users, this approach may need to be re-considered
     * for alteration.
     */
    public void buildTree() {
        VirtualFile wdioFolder = projectWdioRoot(project);
        if (wdioFolder != null) {
            if (data == null) {
                data = new TerraWdioTreeModelDataRoot("Wdio Resources");
            } else {
                Disposer.dispose(rootDisposable);
            }
            rootDisposable = Disposer.newDisposable();
            Disposer.register(rootDisposable, data);

            wdioFolder.refresh(false, true);
            List<VirtualFile> filesAndFoldersInWdioRoot = VfsUtil.collectChildrenRecursively(wdioFolder);
            Set<VirtualFile> specFiles = collectSpecFiles(filesAndFoldersInWdioRoot);
            collectSpecsAndScreenshots(filesAndFoldersInWdioRoot, specFiles, TerraWdioFolders.REFERENCE, AbstractTerraWdioTreeNode::addReference);
            collectSpecsAndScreenshots(filesAndFoldersInWdioRoot, specFiles, TerraWdioFolders.DIFF, (node, vf) -> asScreenshot(node).addDiff(vf));
            collectSpecsAndScreenshots(filesAndFoldersInWdioRoot, specFiles, TerraWdioFolders.LATEST, (node, vf) -> asScreenshot(node).addLatest(vf));

            data.getSpecs().forEach(TerraWdioTreeSpecNode::reorderScreenshotsAlphabeticallyByDisplayName);
        }
    }


    private void collectSpecsAndScreenshots(@NotNull List<VirtualFile> filesAndFoldersInWdioRoot, Set<VirtualFile> specFiles,
                                            @NotNull String imageType, @NotNull VirtualFileToNodeAdder virtualFileToNodeAdder) {
        String wdioRootPath = projectWdioRoot(project).getPath();
        //This will have duplicate folders by name, but they are different folders
        collectSpecFoldersInside(imageType, filesAndFoldersInWdioRoot)
            .forEach(folder -> {
                VirtualFile[] screenshots = VfsUtil.getChildren(folder);
                String folderIdentifier = specFolderIdentifier(folder, project);
                data.getSpecs().stream()
                    .filter(spec -> spec.getDisplayName().equals(folderIdentifier)) //to make sure that the UI tree will contain a single node for a given spec name
                    .findFirst()
                    //If the given spec folder (specNode) has already been added
                    .ifPresentOrElse(specNode -> populateSpecNodeWithFolderAndScreenshots(folder, screenshots, specNode, virtualFileToNodeAdder, imageType),
                        //If a given spec folder hasn't been added
                        () -> {
                            if (existsAfterRefresh(folder)) {
                                TerraWdioTreeSpecNode specNode = TerraWdioTreeNode.forSpec(folderIdentifier);
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
                        });
            });
    }

    private void populateSpecNodeWithFolderAndScreenshots(VirtualFile folder, @NotNull VirtualFile[] screenshots,
                                                          TerraWdioTreeSpecNode specNode, VirtualFileToNodeAdder virtualFileToNodeAdder,
                                                          @NotNull String imageType) {
        //Save folder virtual files only when they belong to the reference version, and not to the latest or diff
        if (TerraWdioFolders.REFERENCE.equals(imageType)) {
            virtualFileToNodeAdder.accept(specNode, folder);
        }
        for (VirtualFile screenshot : screenshots) {
            if (existsAfterRefresh(screenshot)) {
                specNode.findScreenshotNodeByName(screenshot.getName())
                    //If one or more screenshot node have already been added with a given name
                    .ifPresentOrElse(s -> virtualFileToNodeAdder.accept(s, screenshot),
                        //If a screenshot node hasn't been added
                        () -> {
                            TerraWdioTreeScreenshotNode newScreenshotNode = TerraWdioTreeNode.forScreenshot(screenshot.getName());
                            virtualFileToNodeAdder.accept(newScreenshotNode, screenshot);
                            specNode.addScreenshot(newScreenshotNode);
                            Disposer.register(specNode, newScreenshotNode);
                        });
            }
        }
    }

    /**
     * Refreshes the argument virtual file and returns whether it still exists or not.
     */
    public static boolean existsAfterRefresh(@Nullable VirtualFile virtualFile) {
        if (virtualFile != null) {
            virtualFile.refresh(false, virtualFile.isDirectory());
            return virtualFile.exists();
        }
        return false;
    }

    // The methods below are responsible for building the actual tree model from the backing model data.

    @Override
    public Object getRoot() {
        return data;
    }

    @Override
    public Object getChild(Object parent, int index) {
        TerraWdioTreeNode child = null;
        if (parent instanceof TerraWdioTreeModelDataRoot) {
            child = data.getSpecs().get(index);
        } else if (isSpec(parent)) {
            child = asSpec(parent).getScreenshot(index);
        }
        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        int count = 0;
        if (parent instanceof TerraWdioTreeModelDataRoot) {
            count = data.getSpecs().size();
        } else if (isSpec(parent)) {
            count = asSpec(parent).screenshotCount();
        }
        return count;
    }

    @Override
    public boolean isLeaf(Object node) {
        boolean isLeaf = data.getSpecs().isEmpty();
        if (!(node instanceof TerraWdioTreeModelDataRoot)) {
            int index = data.getSpecs().indexOf(node);
            isLeaf = !isSpec(node) || (index != -1 && data.getSpecs().get(index).screenshotCount() == 0);
        }
        return isLeaf;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int indexOfChild = 0;
        if (parent != null && child != null) {
            if (parent instanceof TerraWdioTreeModelDataRoot) {
                indexOfChild = data.getSpecs().indexOf(child);
            } else if (isSpec(parent)) {
                indexOfChild = asSpec(parent).getScreenshots().indexOf(child);
            }
        } else {
            indexOfChild = -1;
        }
        return indexOfChild;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    @FunctionalInterface
    private interface VirtualFileToNodeAdder extends BiConsumer<AbstractTerraWdioTreeNode, VirtualFile> {
    }
}
