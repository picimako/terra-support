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

import static com.intellij.lang.javascript.psi.JSVarStatement.VarKeyword.CONST;
import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isSupportedViewport;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Arrays;
import javax.swing.*;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSVarStatement;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.psi.js.JSArgumentUtil;
import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Provides various checks for the viewports defined in the {@code Terra.describeViewports} block from terra-toolkit.
 * <p>
 * The validations in this inspection all report problems that would block test execution.
 * <p>
 * See the <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md">Terra Webdriver.io Utility Developer's Guide</a>
 *
 * @since 0.1.0
 */
public final class TerraDescribeViewportsBlockingInspection extends TerraWdioInspectionBase {

    @SuppressWarnings("PublicField")
    public boolean reportEmptyViewports = true;
    @SuppressWarnings("PublicField")
    @VisibleForTesting
    public boolean reportNotSupportedViewports = true;
    @SuppressWarnings("PublicField")
    @VisibleForTesting
    public boolean reportNonArrayViewports = true;

    @Override
    @NotNull
    public String getShortName() {
        return "TerraDescribeViewportsBlocking";
    }

    @Override
    public JComponent createOptionsPanel() {
        final MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox("Report empty viewports array", "reportEmptyViewports");
        return panel;
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isTopLevelTerraDescribeViewportsBlock(node)) {
                    JSArgumentUtil.doWithinArgumentListOf(node, argumentList -> {
                        if (argumentList.getArguments().length > 1) {
                            JSExpression viewportList = argumentList.getArguments()[1];
                            if (viewportList instanceof JSArrayLiteralExpression) {
                                final JSExpression[] viewports = ((JSArrayLiteralExpression) viewportList).getExpressions();
                                checkForEmptyViewportsArgument(viewportList, viewports, holder);
                                checkForNotSupportedViewports(viewports, holder);
                            } else {
                                checkForNonArrayTypeViewports(viewportList, holder);
                            }
                        }
                    });
                }
            }
        };
    }

    /**
     * Checks for and registers the whole viewports array either if it's empty, or if all the elements in it are blank.
     */
    private void checkForEmptyViewportsArgument(@NotNull JSExpression viewportList, @NotNull JSExpression[] viewports, @NotNull ProblemsHolder holder) {
        if (reportEmptyViewports) {
            if (viewports.length == 0 || Arrays.stream(viewports).allMatch(vp -> isBlank(getStringValue(vp)))) {
                holder.registerProblem(viewportList, TerraBundle.inspection("no.actual.viewport.specified"));
            }
        }
    }

    /**
     * Validates the viewport values in each {@code Terra.describeViewports} block within a file, and reports the
     * ones that are not one of the supported Terra viewports.
     */
    private void checkForNotSupportedViewports(@NotNull JSExpression[] viewports, @NotNull ProblemsHolder holder) {
        if (reportNotSupportedViewports) {
            for (JSExpression viewport : viewports) {
                if (!isSupportedViewport(viewport)) {
                    holder.registerProblem(viewport, TerraBundle.inspection("viewport.not.supported"), ProblemHighlightType.ERROR);
                }
            }
        }
    }

    /**
     * Validates the viewports argument whether it is defined as an array. There are some exceptions when a problem
     * is not registered:
     * <ul>
     *     <li>when the variable keyword is var or let,</li>
     *     <li>when a variable/constant is used, and it is not initialized at the location of creation,</li>
     *     <li>when a variable/constant is used, and it is initialized as a reference to another variable/constant,</li>
     *     <li>when a variable/constant is used, and it is initialized as a call to a function,</li>
     *     <li>when a function call is used.</li>
     * </ul>
     * <p>
     * The reason these exceptions are in place is because it may be unnecessarily complex to try to find out what values
     * the further references return.
     */
    private void checkForNonArrayTypeViewports(JSExpression viewportList, @NotNull ProblemsHolder holder) {
        if (reportNonArrayViewports) {
            if (viewportList instanceof JSReferenceExpression) {
                PsiElement resolved = ((JSReferenceExpression) viewportList).resolve();
                if (resolved instanceof JSVariable) {
                    JSVarStatement variableStatement = ((JSVariable) resolved).getStatement();
                    if (variableStatement != null
                        && variableStatement.getVarKeyword() == CONST
                        && variableStatement.getDeclarations().length == 1) {

                        JSExpression initializer = variableStatement.getDeclarations()[0].getInitializer();
                        if (initializer != null
                            && !(initializer instanceof JSArrayLiteralExpression)
                            && !(initializer instanceof JSReferenceExpression)
                            && !(initializer instanceof JSCallExpression)) {
                            holder.registerProblem(viewportList, TerraBundle.inspection("non.array.viewports.not.allowed"));
                        }
                    }
                }
            } else if (!(viewportList instanceof JSCallExpression)) {
                holder.registerProblem(viewportList, TerraBundle.inspection("non.array.viewports.not.allowed"));
            }
        }
    }
}
