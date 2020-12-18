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
    @NotNull
    public String getShortName() {
        return "TerraElementValidationIsPreferredOverScreenshot";
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node)) {
                    if (isTerraItMatchesScreenshotExpression(node)) {
                        holder.registerProblem(getTerraValidationFunctionNameElement(node), TerraBundle.inspection("it.validateselement.preferred"));
                    } else if (isTerraValidatesScreenshotExpression(node)) {
                        holder.registerProblem(getTerraValidationFunctionNameElement(node), TerraBundle.inspection("it.validates.element.preferred"));
                    }
                }
            }
        };
    }
}
