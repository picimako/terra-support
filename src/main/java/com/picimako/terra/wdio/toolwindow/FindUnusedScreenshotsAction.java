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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.DirectoryPsiUtil.findDirectory;
import static com.picimako.terra.resources.TerraBundle.toolWindow;
import static com.picimako.terra.wdio.TerraWdioFolders.collectSpecFiles;
import static com.picimako.terra.wdio.TerraWdioFolders.specFileIdentifier;
import static com.picimako.terra.wdio.TerraWdioFolders.wdioRootRelativePath;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import java.util.ArrayList;
import java.util.List;

import com.intellij.icons.AllIcons;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;
import com.picimako.terra.wdio.toolwindow.node.TreeModelDataRoot;
import com.picimako.terra.wdio.toolwindow.node.TerraWdioTree;

/**
 * An action for the Terra Wdio tool window for collecting unused wdio screenshots in the project and marking them
 * appropriately in the tool window.
 * <p>
 * The logic for collecting the unused screenshots is the following:
 * <ol>
 *     <li>Collect all wdio spec files.</li>
 *     <li>Collect all wdio screenshot names (&lt;spec identifier>/&lt;screenshot name>), while also marking them as used.
 *     The latter part is necessary, so that usage information is reset, and in case this action has run before, it won't give false results.</li>
 *     <li>
 *         Iterate through the spec files. For each Terra screenshot validation call, resolve the referenced screenshot name,
 *         then remove it from the collection of screenshot names.
 *     </li>
 *     <li>If any screenshot names remain, those ones will be the unused ones, and they are marked with a red exclamation mark
 *     icon. See {@link TerraWdioTree.TerraWdioNodeRenderer}.</li>
 * </ol>
 * Processing of the screenshot validation calls quits as soon as there is no more screenshot name left in the original
 * collection, meaning that all screenshots are used. This makes sure that no file is processed unnecessarily after
 * the point that it is sure that all screenshots are used.
 * <p>
 * The reason the screenshot names are extended with the spec identifier (the relative path of the spec from the wdio root folder) is
 * that the following:
 * <ul>
 *     <li>let's have two separate spec files, A and B</li>
 *     <li>B contains a screenshot validation for screenshot S</li>
 *     <li>within the reference folder of A there is a screenshot called S too (it is separate from the image for B but has the same name)</li>
 *     <li>if the analysis would be based on solely on the screenshot's name, screenshot S for spec B would be marked as unused, when in fact
 *     it is only in case of A it is unused.</li>
 * </ul>
 *
 * <b>Note:</b>
 * It might happen that all reference versions of a screenshot are removed but the latest and/or diff folder still contain images for it,
 * and one of the spec files still references the image. Since the analysis is based on what is present at code level, this node in the tool
 * window will not be marked as unused, only when the code part of it is removed as well.
 *
 * @see TerraWdioToolWindowFactory
 * @see TerraWdioTree.TerraWdioNodeRenderer
 * @since 0.3.0
 */
final class FindUnusedScreenshotsAction extends AnAction {

    private final ScreenshotNameResolver resolver;
    private final TerraWdioTree tree;

    FindUnusedScreenshotsAction(TerraWdioTree tree, Project project) {
        super(toolWindow("find.unused.screenshots"), toolWindow("find.unused.screenshots.description"), AllIcons.Actions.Execute);
        this.tree = tree;
        resolver = TerraResourceManager.getInstance(project).screenshotNameResolver();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final List<PsiFile> specFiles = new ArrayList<>();
        collectSpecFiles(findDirectory(e.getProject(), wdioRootRelativePath(e.getProject())), specFiles);

        TreeModelDataRoot root = (TreeModelDataRoot) tree.getModel().getRoot();
        final List<String> screenshotPaths = markUsedAndGetAllScreenshotPaths(root);

        for (PsiFile specFile : specFiles) {
            PsiTreeUtil.processElements(specFile, JSCallExpression.class, element -> {
                if (isScreenshotValidationCall(element)) {
                    JSLiteralExpression firstNameArgument = getFirstArgumentAsStringLiteral(element.getArgumentList());
                    screenshotPaths.remove(specFileIdentifier(specFile.getVirtualFile(), e.getProject())
                        + "/"
                        + resolver.resolveWithFallback(firstNameArgument, element.getMethodExpression()));
                }
                return !screenshotPaths.isEmpty();
            });
        }

        markScreenshotsAsUnused(root, screenshotPaths);
        tree.updateUI(); //This is so, that no extra interaction with the tool window is required (focus change, click, etc.) to have the UI updated.
    }

    /**
     * Collects all wdio screenshot paths in the project from the already built wdio tree model.
     * <p>
     * Meanwhile, it also marks all screenshots as used (basically resetting the unused state if it was set).
     */
    private List<String> markUsedAndGetAllScreenshotPaths(TreeModelDataRoot root) {
        final List<String> names = new SmartList<>();
        for (var spec : root.getSpecs()) {
            for (var screenshot : spec.getScreenshots()) {
                screenshot.setUnused(false);
                names.add(spec.getDisplayName() + "/" + screenshot.getDisplayName());
            }
        }
        return names;
    }

    /**
     * Marks all screenshots by the provided names as unused, so that the tool window can display it appropriately later.
     *
     * @param root            the root node of the tree model containing the spec and screenshot nodes
     * @param screenshotPaths the collection of screenshot names to mark unused
     */
    private void markScreenshotsAsUnused(TreeModelDataRoot root, List<String> screenshotPaths) {
        for (var spec : root.getSpecs()) {
            for (var screenshot : spec.getScreenshots()) {
                for (String screenshotPath : screenshotPaths) {
                    if (screenshotPath.equals(spec.getDisplayName() + "/" + screenshot.getDisplayName())) {
                        screenshot.setUnused(true);
                        break;
                    }
                }
            }
        }
    }
}
