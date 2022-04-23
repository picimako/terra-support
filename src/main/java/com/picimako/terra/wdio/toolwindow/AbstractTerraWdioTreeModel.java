//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.toolwindow;

import static com.picimako.terra.wdio.TerraWdioFolders.collectSpecFiles;
import static com.picimako.terra.wdio.TerraWdioFolders.existsAfterRefresh;
import static com.picimako.terra.wdio.TerraWdioFolders.projectWdioRoot;
import static com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode.asScreenshot;

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

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.toolwindow.node.AbstractTerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTreeNode;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TreeScreenshotNode;
import com.picimako.terra.wdio.toolwindow.node.TreeSpecNode;

/**
 * Contains common logic for building tree models.
 *
 * @see TerraWdioTreeModel
 */
public abstract class AbstractTerraWdioTreeModel implements TreeModel {

    private Disposable rootDisposable;
    protected TreeModelDataRoot data;
    protected final Project project;

    protected AbstractTerraWdioTreeModel(Project project) {
        this.project = project;
        buildTree();
    }

    /**
     * Builds the contents of the tree model that is shown in the tool window.
     * <p>
     * It uses only a single wdio root path among the ones defined in the Terra Support Settings, first found in the project.
     * <p>
     * <b>Disposer logic</b>
     * <p>
     * Clears all virtual files and nodes saved in the model.
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
                data = new TreeModelDataRoot(TerraBundle.toolWindow("root.node.name"), project);
            } else {
                Disposer.dispose(rootDisposable);
            }
            rootDisposable = Disposer.newDisposable();
            Disposer.register(rootDisposable, data);

            wdioFolder.refresh(false, true);
            List<VirtualFile> filesAndFoldersAnywhereInWdioRoot = VfsUtil.collectChildrenRecursively(wdioFolder);
            Set<VirtualFile> specFiles = collectSpecFiles(filesAndFoldersAnywhereInWdioRoot);
            collectSpecsAndScreenshots(filesAndFoldersAnywhereInWdioRoot, specFiles, TerraWdioFolders.REFERENCE, AbstractTerraWdioTreeNode::addReference);
            collectSpecsAndScreenshots(filesAndFoldersAnywhereInWdioRoot, specFiles, TerraWdioFolders.DIFF, (node, vf) -> asScreenshot(node).addDiff(vf));
            collectSpecsAndScreenshots(filesAndFoldersAnywhereInWdioRoot, specFiles, TerraWdioFolders.LATEST, (node, vf) -> asScreenshot(node).addLatest(vf));

            data.getSpecs().forEach(TreeSpecNode::reorderScreenshotsAlphabeticallyByDisplayName);
        }
    }

    protected void populateSpecNodeWithFolderAndScreenshots(VirtualFile folder, @NotNull VirtualFile[] screenshots,
                                                            TreeSpecNode specNode, VirtualFileToNodeAdder virtualFileToNodeAdder,
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
                            TreeScreenshotNode newScreenshotNode = TerraWdioTreeNode.forScreenshot(screenshot.getName(), project);
                            virtualFileToNodeAdder.accept(newScreenshotNode, screenshot);
                            specNode.addScreenshot(newScreenshotNode);
                            Disposer.register(specNode, newScreenshotNode);
                        });
            }
        }
    }

    protected abstract void collectSpecsAndScreenshots(@NotNull List<VirtualFile> filesAndFoldersAnywhereInWdioRoot, Set<VirtualFile> specFiles,
                                                       @NotNull String imageType, @NotNull VirtualFileToNodeAdder virtualFileToNodeAdder);

    // The methods below are responsible for building the actual tree model from the backing model data.

    @Override
    public Object getRoot() {
        return data;
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
    protected interface VirtualFileToNodeAdder extends BiConsumer<AbstractTerraWdioTreeNode, VirtualFile> {
    }
}
