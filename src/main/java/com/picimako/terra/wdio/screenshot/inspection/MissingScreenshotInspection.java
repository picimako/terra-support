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

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraElementOrScreenshotValidation;

import com.intellij.codeInspection.LocalInspectionToolSession;
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

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;
import com.picimako.terra.wdio.screenshot.TerraScreenshotCollector;

/**
 * Based on the name parameter of Terra.it and Terra.validates calls, it checks whether there are any reference
 * screenshot exists, and if there's none, then reports the problem.
 * <p>
 * In case the name parameter is not specified, the default value {@code default} is used for the screenshot lookup.
 *
 * @since 0.2.0
 */
public class MissingScreenshotInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isTerraElementOrScreenshotValidation(node)) {
                    JSExpression callExpression = node.getExpression();
                    if (callExpression instanceof JSCallExpression) {
                        TerraScreenshotCollector screenshotCollector = new TerraScreenshotCollector(holder.getProject());
                        JSLiteralExpression nameExpr = JSPsiUtil.getFirstArgumentAsStringLiteral(((JSCallExpression) callExpression).getArgumentList());
                        if (nameExpr != null) {
                            if (screenshotCollector.collectFor(nameExpr).length == 0) {
                                holder.registerProblem(nameExpr, TerraBundle.inspection("no.screenshot.exists"), ProblemHighlightType.ERROR);
                            }
                        } else {
                            JSExpression methodExpression = ((JSCallExpression) callExpression).getMethodExpression();
                            if (methodExpression != null && screenshotCollector.collectForDefault(methodExpression).length == 0)
                                holder.registerProblem(methodExpression, TerraBundle.inspection("no.screenshot.exists.for.default"), ProblemHighlightType.ERROR);
                        }
                    }
                }
            }
        };
    }
}
