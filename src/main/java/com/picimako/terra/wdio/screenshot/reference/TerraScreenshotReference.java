//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.reference;

import static com.picimako.terra.wdio.TerraWdioFolders.isReferenceScreenshot;

import java.util.Arrays;
import java.util.Comparator;

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

import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;

/**
 * A reference implementation for Terra screenshots. The reference is for placing it on screenshot validation calls
 * (see the javadoc of {@link com.picimako.terra.wdio.screenshot.ScreenshotNameResolver#resolveName(JSLiteralExpression)}
 * and it's implementations' documentation).
 * <p>
 * Although file search based on the image's name would return the latest and diff versions of images, the list is filtered,
 * so that only the reference ones are returned for now. The list of suggestions is sorted alphabetically by the context
 * string (theme, viewport, locale, browser).
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
        screenshotCollector = TerraScreenshotCollector.getInstance(element.getProject());

    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (!incompleteCode) {
            var screenshotsForName = screenshotCollector.collectAsPsiFilesFor((JSLiteralExpression) myElement);
            return screenshotsForName.length > 0 ? createResultItemsFor(screenshotsForName, myElement.getProject()) : ResolveResult.EMPTY_ARRAY;
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    private ResolveResult[] createResultItemsFor(PsiFile[] screenshotsForName, Project project) {
        return Arrays.stream(screenshotsForName)
            .filter(screenshot -> isReferenceScreenshot(screenshot.getVirtualFile(), project)) //show only reference images
            .map(TerraScreenshotPsiFile::new)
            .sorted(Comparator.comparing(o -> o.getPresentation().getLocationString())) //TerraScreenshotPsiFile always has  ItemPresentation
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @Override
    public @Nullable PsiElement resolve() {
        var resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
