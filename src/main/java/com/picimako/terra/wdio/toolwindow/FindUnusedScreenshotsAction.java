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
import static com.picimako.terra.wdio.TerraWdioFolders.wdioRootRelativePath;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.intellij.icons.AllIcons;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.screenshot.TerraScreenshotNameResolver;

/**
 * An action for the Terra Wdio tool window for collecting unused wdio screenshots in the project and marking them
 * appropriately in the tool window.
 * <p>
 * The logic for collecting the unused screenshots is the following:
 * <ul>
 *     <li>Collect all wdio spec files.</li>
 *     <li>Collect all wdio screenshot names, while also marking them as used. The latter part is necessary, so that
 *     usage information is reset, so that in case this action has run before, it won't give false results.</li>
 *     <li>
 *         Iterate through the spec files. For each Terra screenshot validation call, resolve the referenced screenshot name,
 *         then remove it from the collection of screenshot names.
 *     </li>
 *     <li>If any screenshot names remain, those ones will be the unused ones, and they are marked with a red exclamation mark
 *     icon. See {@link TerraWdioTreeCellRenderer}.</li>
 * </ul>
 * Processing the screenshot validation calls quits as soon as there are no more screenshot names left in the original
 * collection, meaning that all screenshots are used. This makes sure that no files are processed unnecessarily after
 * the point that it is sure that all screenshots are used.
 *
 * @see TerraWdioToolWindowFactory
 * @see TerraWdioTreeCellRenderer
 * @since 0.3.0
 */
final class FindUnusedScreenshotsAction extends AnAction {

    private final TerraScreenshotNameResolver resolver = new TerraScreenshotNameResolver();
    private final TerraWdioTree tree;

    FindUnusedScreenshotsAction(TerraWdioTree tree) {
        super(toolWindow("find.unused.screenshots"), toolWindow("find.unused.screenshots.description"), AllIcons.Actions.Execute);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final List<PsiFile> specFiles = new ArrayList<>();
        collectSpecFiles(findDirectory(e.getProject(), wdioRootRelativePath(e.getProject())), specFiles);

        TerraWdioTreeModelDataRoot root = (TerraWdioTreeModelDataRoot) tree.getModel().getRoot();
        final List<String> screenshotNames = markUsedAndGetAllScreenshotNames(root);

        for (PsiFile specFile : specFiles) {
            PsiTreeUtil.processElements(specFile, JSCallExpression.class, element -> {
                if (isScreenshotValidationCall(element)) {
                    JSLiteralExpression firstNameArgument = getFirstArgumentAsStringLiteral(element.getArgumentList());
                    screenshotNames.remove(resolver.resolveWithFallback(firstNameArgument, element.getMethodExpression()));
                }
                return !screenshotNames.isEmpty();
            });
        }

        markScreenshotsAsUnused(root, screenshotNames);
        tree.updateUI(); //This is so, that no extra interaction with the tool window is required (focus change, click, etc.) to have the UI updated.
    }

    /**
     * Collects all wdio screenshot names from the already built wdio tree model.
     * <p>
     * Meanwhile it also marks all screenshots as used (basically resetting the unused state if it was set).
     *
     * @return the list of screenshot names in the project
     */
    private List<String> markUsedAndGetAllScreenshotNames(TerraWdioTreeModelDataRoot root) {
        final List<String> names = new ArrayList<>();
        root.getSpecs()
            .stream()
            .flatMap(spec -> spec.getScreenshots().stream())
            .forEach(screenshot -> {
                screenshot.setUnused(false);
                names.add(screenshot.getDisplayName());
            });
        return names;
    }

    /**
     * Marks all screenshots by the provided names as unused, so that the tool window can display it appropriately later.
     *
     * @param root            the root node of the tree model containing the spec and screenshot nodes
     * @param screenshotNames the collection of screenshot names to mark unused
     */
    private void markScreenshotsAsUnused(TerraWdioTreeModelDataRoot root, List<String> screenshotNames) {
        screenshotNames.forEach(name -> root.getSpecs()
            .stream()
            .flatMap(spec -> spec.findScreenshotNodeByName(name).stream())
            .filter(Objects::nonNull)
            .forEach(screenshot -> screenshot.setUnused(true)));
    }
}
