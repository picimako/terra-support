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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.isJSStringLiteral;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * Adds references to the first, name parameters of screenshot validation helpers
 * (see {@link com.picimako.terra.wdio.TerraWdioPsiUtil#SCREENSHOT_VALIDATION_NAMES}), so that they are linked to their
 * corresponding screenshot image files.
 * <p>
 * The first, name parameter, of Terra screenshot validation calls, is optional (in that case the default value
 * {@code default} is generated into the name by Terra), so when it is missing, there is no element to create the reference for,
 * thus that case is not yet supported.
 *
 * @since 0.2.0
 */
public class TerraScreenshotReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JSLiteralExpression.class),
            new PsiReferenceProvider() {
                @Override
                public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    if (isJSStringLiteral(element)) {
                        PsiElement parentDescribeCall = PsiTreeUtil.findFirstParent(element,
                            psiElement -> psiElement instanceof JSCallExpression && isScreenshotValidationCall((JSCallExpression) psiElement));
                        if (parentDescribeCall != null) {
                            JSLiteralExpression describeBlockName = getFirstArgumentAsStringLiteral(((JSCallExpression) parentDescribeCall).getArgumentList());
                            if (describeBlockName == element) {
                                return new PsiReference[]{new TerraScreenshotReference(element)};
                            }
                        }
                    }
                    return PsiReference.EMPTY_ARRAY;
                }
            });
    }
}
