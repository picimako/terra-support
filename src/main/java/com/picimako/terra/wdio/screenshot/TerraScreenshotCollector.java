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

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.TerraWdioFolders.isReferenceScreenshot;
import static com.picimako.terra.wdio.TerraWdioFolders.specFileIdentifier;
import static com.picimako.terra.wdio.TerraWdioFolders.specFolderIdentifier;

import java.util.Arrays;
import java.util.function.Supplier;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Collects screenshots based on various expressions.
 * <p>
 * The spec files and snapshots folders are related to each other as follows:
 * <pre>
 * tests
 *      wdio
 *          __snapshots__       <-- This contains the screenshots for some-spec.js.
 *          nestedFolder
 *              __snapshots__   <-- This contains the screenshots for another-spec.js.
 *              another-spec.js
 *          some-spec.js
 * </pre>
 * Screenshots are collected based on the referenced screenshots' names and also based on which spec file a
 * screenshot validation call is implemented in. The latter one makes sure that related screenshots are returned only
 * for the current spec file, and not for other specs, if there happens to be a screenshot with the same name for that too.
 */
public class TerraScreenshotCollector {

    private final TerraScreenshotNameResolver screenshotNameResolver = new TerraScreenshotNameResolver();
    private final Project project;

    public TerraScreenshotCollector(Project project) {
        this.project = project;
    }

    /**
     * Collects screenshots from the current project based on the provided JS literal expression.
     *
     * @param element the literal expression based on whose text the search is performed
     * @return the array of screenshots found as PsiElements
     */
    @NotNull
    public PsiElement[] collectFor(JSLiteralExpression element) {
        return collect(element, () -> screenshotNameResolver.resolveName(element));
    }

    /**
     * Collects screenshots from the current project based on the provided JS literal expression.
     *
     * @param element the literal expression based on whose text the search is performed
     * @return the array of screenshots files found as PsiFiles
     */
    @NotNull
    public PsiFile[] collectAsPsiFilesFor(JSLiteralExpression element) {
        return Arrays.stream(collect(element, () -> screenshotNameResolver.resolveName(element)))
            .map(e -> (PsiFile) e)
            .toArray(PsiFile[]::new);
    }

    /**
     * Collects screenshots from the current project based on the provided expression.
     * <p>
     * This is necessary when a Terra call doesn't have its name parameter specified, so that the parent describe block
     * is determined based on the call's method expression instead of the name parameter, using the default name.
     *
     * @param methodExpression the method expression based on whose text the search is performed
     * @return the array of screenshots found as PsiElements
     */
    @NotNull
    public PsiElement[] collectForDefault(JSExpression methodExpression) {
        return collect(methodExpression, () -> screenshotNameResolver.resolveDefaultName(methodExpression));
    }

    /**
     * Collects the reference screenshots with the provided name.
     * <p>
     * The argument Psi element is designed to be a Terra.it or Terra.validates call, or its name parameter, based on
     * which the containing file's {@code __snapshots__} folder is determined, in which collecting the screenshots happens.
     * <p>
     * The {@code __snapshots__} folder related to a spec file is always in the same directory as the spec file.
     *
     * @param element      the element to determine the __snapshots__ folder for the collection
     * @param nameSupplier provides the name of the screenshots to find
     * @return the array of screenshots found
     */
    private PsiElement[] collect(PsiElement element, Supplier<String> nameSupplier) {
        PsiDirectory specFileDirectory = element.getContainingFile().getParent();
        if (specFileDirectory != null) {
            PsiDirectory snapshotsDirectory = specFileDirectory.findSubdirectory(TerraWdioFolders.SNAPSHOTS);
            if (snapshotsDirectory != null) {
                String specFileId = specFileIdentifier(element.getContainingFile().getVirtualFile(), project);
                return PsiTreeUtil.collectElements(snapshotsDirectory,
                    e -> {
                        if (e instanceof PsiBinaryFile) {
                            VirtualFile screenshotFile = ((PsiBinaryFile) e).getVirtualFile();
                            return isReferenceScreenshot(screenshotFile)
                                && ((PsiBinaryFile) e).getName().equals(nameSupplier.get()) //matching based on expected screenshot name
                                && specFolderIdentifier(screenshotFile.getParent(), project).equals(specFileId); //matching based on the containing spec file

                        }
                        return false;
                    }
                );
            }
        }
        return PsiElement.EMPTY_ARRAY;
    }
}
