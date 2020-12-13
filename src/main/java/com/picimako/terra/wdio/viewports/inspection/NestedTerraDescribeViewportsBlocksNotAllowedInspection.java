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

package com.picimako.terra.wdio.viewports.inspection;

import static com.picimako.terra.FileTypePreconditionsUtil.isInWdioSpecFile;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getMethodExpressionOf;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Reports {@code Terra.describeViewports} blocks that are not top-level ones.
 *
 * @since 0.1.0
 */
public class NestedTerraDescribeViewportsBlocksNotAllowedInspection extends TerraWdioInspectionBase {

    private static final String NESTED_BLOCKS_ARE_NOT_ALLOWED_MESSAGE = "Nested Terra.describeViewports blocks are not allowed.";

    @Override
    public @NotNull String getShortName() {
        return "NestedTerraDescribeViewportsBlocksNotAllowed";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isNestedTerraDescribeViewportsBlock(node)) {
                    //At this point getMethodExpressionOf() should not return null because it has been validated in isNestedTerraDescribeViewportsBlock().
                    holder.registerProblem(getMethodExpressionOf(node), NESTED_BLOCKS_ARE_NOT_ALLOWED_MESSAGE);
                }
            }
        };
    }
}
