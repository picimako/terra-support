//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerraFunctionalTesting;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isNonTerraItElementOrScreenshotValidation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.psi.js.JSArgumentUtil;
import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;
import com.picimako.terra.wdio.TerraWdioPsiUtil;

/**
 * Reports problems when the screenshot name parameter of {@code Terra.validates.element} and
 * {@code Terra.validates screenshot} calls are missing, since it is mandatory in terra-functional-testing.
 * <p>
 * This inspection applies only to terra-functional-testing, so it is not enforced for terra-toolkit here either.
 *
 * @since 0.6.0
 */
public class MissingScreenshotNameInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerraFunctionalTesting(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                if (isNonTerraItElementOrScreenshotValidation(node)) {
                    String nameArgument = JSPsiUtil.getFirstArgumentAsString(JSArgumentUtil.getArgumentListOf(node));
                    if (nameArgument == null) {
                        var methodExpression = TerraWdioPsiUtil.getMethodExpressionOf(node);
                        if (methodExpression != null) {
                            holder.registerProblem(methodExpression, TerraBundle.inspection("screenshot.name.argument.missing"));
                        }
                    }
                }
            }
        };
    }
}
