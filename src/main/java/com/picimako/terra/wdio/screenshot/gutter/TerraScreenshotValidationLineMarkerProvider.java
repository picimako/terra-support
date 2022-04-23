//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.gutter;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerraToolkit;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import java.util.Collection;
import javax.swing.*;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.images.fileTypes.impl.ImageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioPsiUtil;
import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;

/**
 * Provides line markers for Terra screenshot validation calls in wdio spec files.
 * <p>
 * Line markers are added for calls with or without a name parameter present, however references to the screenshots
 * are added only when the call references a "default" screenshot (with no name parameter). This way the screenshot
 * references added to those name parameters are complemented with this logic.
 * <p>
 * In case the call references a non-existent screenshot, the line marker is not added.
 * Instead they will be marked by the {@link com.picimako.terra.wdio.screenshot.inspection.MissingScreenshotInspection}.
 *
 * @since 0.4.0
 */
public class TerraScreenshotValidationLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (isUsingTerra(element.getProject()) && element instanceof JSCallExpression && isScreenshotValidationCall((JSCallExpression) element)) {
            JSCallExpression terraCallExpr = (JSCallExpression) element;
            JSLiteralExpression nameArgument = getFirstArgumentAsStringLiteral(terraCallExpr.getArgumentList());
            if (isUsingTerraToolkit(element.getProject()) && nameArgument == null) {
                JSExpression methodExpression = terraCallExpr.getMethodExpression();
                if (methodExpression != null) {
                    PsiElement[] screenshots = new TerraScreenshotCollector(element.getProject()).collectForDefault(methodExpression);
                    if (screenshots.length > 0) {
                        PsiElement leafElement = findLeafElement(terraCallExpr);
                        if (leafElement != null) {
                            result.add(NavigationGutterIconBuilder.create(ImageFileType.INSTANCE.getIcon())
                                .setTargets(screenshots)
                                .setTooltipText(TerraBundle.message("terra.wdio.screenshot.gutter.navigate.to.related"))
                                .createLineMarkerInfo(leafElement.getFirstChild()));
                        }
                    }
                }
            } else if (nameArgument != null) {
                result.add(NavigationGutterIconBuilder.create(ImageFileType.INSTANCE.getIcon())
                    .setTargets(PsiElement.EMPTY_ARRAY)
                    .setTooltipText(TerraBundle.message("terra.wdio.screenshot.gutter.validation.on.this.line"))
                    .createLineMarkerInfo(nameArgument.getFirstChild()));
            }
        }
    }

    @Nullable
    private PsiElement findLeafElement(JSCallExpression callExpression) {
        Collection<JSReferenceExpression> references = PsiTreeUtil.findChildrenOfType(callExpression, JSReferenceExpression.class);
        return references.stream().filter(reference -> TerraWdioPsiUtil.TERRA.equals(reference.getText())).findFirst().orElse(null);
    }

    @Override
    public String getName() {
        return TerraBundle.message("terra.screenshot.linemarker.name");
    }

    @Override
    public @Nullable Icon getIcon() {
        return ImageFileType.INSTANCE.getIcon();
    }
}
