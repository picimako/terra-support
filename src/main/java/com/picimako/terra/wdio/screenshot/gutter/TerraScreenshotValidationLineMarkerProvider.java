/*
 * Copyright 2021 Tamás Balog
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

package com.picimako.terra.wdio.screenshot.gutter;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import java.util.Collection;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import icons.ImagesIcons;
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
 */
public class TerraScreenshotValidationLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof JSCallExpression && isScreenshotValidationCall((JSCallExpression) element)) {
            JSCallExpression terraCallExpr = (JSCallExpression) element;
            JSLiteralExpression nameArgument = getFirstArgumentAsStringLiteral(terraCallExpr.getArgumentList());
            if (nameArgument == null) {
                JSExpression methodExpression = terraCallExpr.getMethodExpression();
                if (methodExpression != null) {
                    PsiElement[] screenshots = new TerraScreenshotCollector(element.getProject()).collectForDefault(methodExpression);
                    if (screenshots.length > 0) {
                        PsiElement leafElement = findLeafElement(terraCallExpr);
                        if (leafElement != null) {
                            result.add(NavigationGutterIconBuilder.create(ImagesIcons.ImagesFileType)
                                .setTargets(screenshots)
                                .setTooltipText(TerraBundle.message("terra.wdio.screenshot.gutter.navigate.to.related"))
                                .createLineMarkerInfo(leafElement.getFirstChild()));
                        }
                    }
                }
            } else {
                result.add(NavigationGutterIconBuilder.create(ImagesIcons.ImagesFileType)
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
}
