//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
import com.intellij.lang.javascript.psi.JSExpressionStatement;
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
            public void visitJSExpressionStatement(@NotNull JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);
                if (!isTerraElementOrScreenshotValidation(node)) return;

                var callExpression = node.getExpression();
                if (callExpression instanceof JSCallExpression) {
                    var screenshotCollector = TerraScreenshotCollector.getInstance(holder.getProject());
                    var nameExpr = JSPsiUtil.getFirstArgumentAsStringLiteral(((JSCallExpression) callExpression).getArgumentList());
                    if (nameExpr != null) {
                        if (screenshotCollector.collectFor(nameExpr).length == 0) {
                            holder.registerProblem(nameExpr, TerraBundle.inspection("no.screenshot.exists"), ProblemHighlightType.ERROR);
                        }
                        return;
                    }
                    var methodExpression = ((JSCallExpression) callExpression).getMethodExpression();
                    if (methodExpression != null && screenshotCollector.collectForDefault(methodExpression).length == 0) {
                        holder.registerProblem(methodExpression, TerraBundle.inspection("no.screenshot.exists.for.default"), ProblemHighlightType.ERROR);
                    }
                }
            }
        };
    }
}
