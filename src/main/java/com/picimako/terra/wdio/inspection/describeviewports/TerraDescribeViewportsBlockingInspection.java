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

package com.picimako.terra.wdio.inspection.describeviewports;

import static com.picimako.terra.FileTypePreconditionsUtil.isInWdioSpecFile;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isSupportedViewport;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Arrays;
import javax.swing.*;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.psi.js.JSArgumentUtil;
import com.picimako.terra.wdio.inspection.TerraWdioInspectionBase;

/**
 * Provides various checks for the viewports defined in the {@code Terra.describeViewports} block from terra-toolkit.
 * <p>
 * The validations in this inspection all report problems that would block test execution.
 * <p>
 * See the <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md">Terra Webdriver.io Utility Developer's Guide</a>
 */
public final class TerraDescribeViewportsBlockingInspection extends TerraWdioInspectionBase {

    private static final String THIS_VIEWPORT_IS_NOT_SUPPORTED_BY_TERRA_MESSAGE = "This viewport is not supported by Terra.";
    private static final String THERE_IS_NO_ACTUAL_VIEWPORT_SPECIFIED_MESSAGE = "There is no actual viewport specified.";
    private static final String NON_ARRAY_VIEWPORTS_NOT_ALLOWED_MESSAGE = "Non-array-type values are not allowed for the viewports argument.";

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
//        panel.addCheckbox("Report not supported viewport values", "reportNotSupportedViewports");
//        panel.addCheckbox("Report non-array-type viewports", "reportNonArrayViewports");
        return panel;
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTopLevelTerraDescribeViewportsBlock(node)) { //FIXME: runs into each Terra.describeViewports twice
                    JSArgumentUtil.doWithinArgumentListOf(node, argumentList -> {
                        if (argumentList.getArguments().length > 1) {
                            JSExpression viewportList = argumentList.getArguments()[1];
                            if (viewportList instanceof JSArrayLiteralExpression) {
                                final JSExpression[] viewports = ((JSArrayLiteralExpression) viewportList).getExpressions();
                                checkForEmptyViewportsArgument(viewportList, viewports, holder);
                                checkForNotSupportedViewports(viewports, holder);
                            } else if (reportNonArrayViewports) {
                                holder.registerProblem(viewportList, NON_ARRAY_VIEWPORTS_NOT_ALLOWED_MESSAGE);
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
                holder.registerProblem(viewportList, THERE_IS_NO_ACTUAL_VIEWPORT_SPECIFIED_MESSAGE);
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
                    holder.registerProblem(viewport, THIS_VIEWPORT_IS_NOT_SUPPORTED_BY_TERRA_MESSAGE, ProblemHighlightType.ERROR);
                }
            }
        }
    }
}
