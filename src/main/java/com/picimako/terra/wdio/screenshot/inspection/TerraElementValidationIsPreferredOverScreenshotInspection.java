//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraItMatchesScreenshot;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Highlights {@code Terra.validates.screenshot()} and {@code Terra.it.matchesScreenshot()} calls because their
 * corresponding {@code Terra.validates.element()} and {@code Terra.it.validatesElement()} calls are preferred, due
 * to potential future deprecation and removal of {@code Terra.should.matchScreenshot()}.
 *
 * @since 0.1.0
 */
public class TerraElementValidationIsPreferredOverScreenshotInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isTerraItMatchesScreenshot(node)) {
                    holder.registerProblem(getTerraValidationFunctionNameElement(node), TerraBundle.inspection("it.validateselement.preferred"));
                } else if (isTerraValidatesScreenshotExpression(node)) {
                    holder.registerProblem(getTerraValidationFunctionNameElement(node), TerraBundle.inspection("validates.element.preferred"));
                }
            }
        };
    }
}
