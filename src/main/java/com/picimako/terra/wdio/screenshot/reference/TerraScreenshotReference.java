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

package com.picimako.terra.wdio.screenshot.reference;

import java.util.Arrays;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.TerraWdioFolders;
import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;
import com.picimako.terra.wdio.screenshot.TerraScreenshotNameResolver;

/**
 * A reference implementation for Terra screenshots. The reference is for placing it on screenshot validation calls
 * (see the javadoc of {@link TerraScreenshotNameResolver#resolveName(JSLiteralExpression)} and it's class-level documentation).
 * <p>
 * Although file search based on the image's name would return the latest and diff versions of images, the list is filtered,
 * so that only the reference ones are returned for now.
 * <p>
 * NOTE: If there will be demand for showing latest and diff images as well in the suggestion list,
 * it may be implemented later, based on that demand.
 *
 * @since 0.2.0
 */
public class TerraScreenshotReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final TerraScreenshotCollector screenshotCollector;

    /**
     * The text range of the reference will be the String literal without the surrounding apostrophes and double-quote characters.
     */
    public TerraScreenshotReference(@NotNull PsiElement element) {
        super(element, TextRange.create(1, element.getTextRange().getLength() - 1), true);
        screenshotCollector = new TerraScreenshotCollector(element.getProject());
    }

    @Override
    public @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
        if (!incompleteCode) {
            PsiFile[] screenshotsForName = screenshotCollector.collectAsPsiFilesFor((JSLiteralExpression) myElement);
            //TODO: idea: reorder the suggestions based on locale, browser and viewport to get a consistently ordered list
            return screenshotsForName.length > 0 ? createResultItemsFor(screenshotsForName, myElement.getProject()) : ResolveResult.EMPTY_ARRAY;
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    private ResolveResult[] createResultItemsFor(PsiFile[] screenshotsForName, Project project) {
        return Arrays.stream(screenshotsForName)
            .filter(screenshot -> TerraWdioFolders.isReferenceScreenshot(screenshot.getVirtualFile(), project)) //show only reference images
            .map(TerraScreenshotPsiFile::new)
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
