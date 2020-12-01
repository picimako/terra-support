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

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditionsUtil.isInWdioSpecFile;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraWdioInspectionBase;
import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;

/**
 * Based on the name parameter of Terra.it and Terra.validates calls, it checks whether there are any reference
 * screenshot exists, and if there's none, then reports the problem.
 * <p>
 * In case the name parameter is not specified, the default value {@code default} is used for the screenshot lookup.
 * <p>
 * For screenshot name resolution see {@link com.picimako.terra.wdio.screenshot.reference.TerraScreenshotNameResolver}.
 */
public class MissingScreenshotInspection extends TerraWdioInspectionBase {

    private static final String NO_SCREENSHOTS_EXISTS_MESSAGE = "No reference screenshot exists for any context, for the name specified.";
    private static final String NO_SCREENSHOTS_EXISTS_FOR_DEFAULT_MESSAGE = "No reference screenshot exists for any context, for the default name.";

    private final TerraScreenshotCollector screenshotCollector = new TerraScreenshotCollector();

    @Override
    public @NotNull String getShortName() {
        return "MissingScreenshot";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTerraElementOrScreenshotValidationFunction(node)) {
                    JSExpression callExpression = node.getExpression();
                    if (callExpression instanceof JSCallExpression) {
                        JSLiteralExpression nameExpr = JSPsiUtil.getFirstArgumentAsStringLiteral(((JSCallExpression) callExpression).getArgumentList());
                        if (nameExpr != null) {
                            if (screenshotCollector.collectFor(nameExpr).length == 0) {
                                holder.registerProblem(nameExpr, NO_SCREENSHOTS_EXISTS_MESSAGE, ProblemHighlightType.ERROR);
                            }
                        } else {
                            JSExpression methodExpression = ((JSCallExpression) callExpression).getMethodExpression();
                            if (methodExpression != null && screenshotCollector.collectForDefault(methodExpression).length == 0)
                                holder.registerProblem(methodExpression, NO_SCREENSHOTS_EXISTS_FOR_DEFAULT_MESSAGE, ProblemHighlightType.ERROR);
                        }
                    }
                }
            }
        };
    }
}
