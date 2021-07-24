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
    public @NotNull String getShortName() {
        return "MissingScreenshotName";
    }

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
