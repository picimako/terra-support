//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.TerraWdioFolders.isReferenceScreenshot;
import static com.picimako.terra.wdio.TerraWdioFolders.specFileIdentifier;
import static com.picimako.terra.wdio.TerraWdioFolders.specFolderIdentifier;

import java.util.Arrays;
import java.util.function.Supplier;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraWdioFolders;

/**
 * Collects screenshots based on various expressions.
 * <p>
 * It is implemented as a project service, and this way during {@link com.picimako.terra.wdio.screenshot.gutter.TerraScreenshotValidationLineMarkerProvider}
 * and {@link com.picimako.terra.wdio.screenshot.inspection.MissingScreenshotInspection} the number of instance created
 * of this object can be minimized.
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
@Service(Service.Level.PROJECT)
public final class TerraScreenshotCollector {
    private final ScreenshotNameResolver screenshotNameResolver;
    private final Project project;

    public TerraScreenshotCollector(Project project) {
        this.project = project;
        screenshotNameResolver = TerraResourceManager.getInstance(project).screenshotNameResolver();
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
            .map(PsiFile.class::cast)
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
        if (element == null || nameSupplier.get() == null) {
            return PsiElement.EMPTY_ARRAY;
        }

        var specFileDirectory = element.getContainingFile().getParent();
        if (specFileDirectory != null) {
            var snapshotsDirectory = specFileDirectory.findSubdirectory(TerraWdioFolders.SNAPSHOTS);
            if (snapshotsDirectory != null) {
                String specFileId = specFileIdentifier(element.getContainingFile().getVirtualFile(), project);
                return PsiTreeUtil.collectElements(snapshotsDirectory,
                    e -> {
                        if (e instanceof PsiBinaryFile) {
                            var screenshotFile = ((PsiBinaryFile) e).getVirtualFile();
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

    public static TerraScreenshotCollector getInstance(Project project) {
        return project.getService(TerraScreenshotCollector.class);
    }
}
