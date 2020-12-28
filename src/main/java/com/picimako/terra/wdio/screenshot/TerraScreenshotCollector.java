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

import java.util.Arrays;
import java.util.function.Supplier;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.screenshot.reference.TerraScreenshotNameResolver;

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
 */
public class TerraScreenshotCollector {

    private final TerraScreenshotNameResolver screenshotNameResolver = new TerraScreenshotNameResolver();

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
        return PsiTreeUtil.collectElements(
            element.getContainingFile().getParent().findSubdirectory(TerraWdioFolders.SNAPSHOTS),
            e -> e instanceof PsiBinaryFile
                && isReferenceScreenshot(((PsiBinaryFile) e).getVirtualFile())
                && ((PsiBinaryFile) e).getName().equals(nameSupplier.get())
        );
    }
}
