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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getCallExpression;
import static com.picimako.terra.FileTypePreconditions.isInWdioSpecFile;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isSupportedViewport;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.psi.js.JSLiteralExpressionUtil;
import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioPsiUtil;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Provides various checks for the viewports defined in the {@code Terra.describeViewports} block from terra-toolkit.
 * <p>
 * The validations in this inspection all report problems that would not block test execution.
 * <p>
 * See the <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md">Terra Webdriver.io Utility Developer's Guide</a>
 *
 * @since 0.1.0
 */
public final class TerraDescribeViewportsInspection extends TerraWdioInspectionBase {

    @SuppressWarnings("PublicField")
    public boolean reportDuplicateViewports = true;
    @SuppressWarnings("PublicField")
    public boolean reportViewportsNotInAscendingOrder = true;

    @Override
    @NotNull
    public String getShortName() {
        return "TerraDescribeViewports";
    }

    @Override
    public JComponent createOptionsPanel() {
        final MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox("Report duplicate viewport values", "reportDuplicateViewports");
        panel.addCheckbox("Report when viewports are not in ascending order", "reportViewportsNotInAscendingOrder");
        return panel;
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTopLevelTerraDescribeViewportsBlock(node)) {
                    JSCallExpression terraDescribeViewports = getCallExpression(node);
                    if (terraDescribeViewports != null) {
                        JSArgumentList argumentList = terraDescribeViewports.getArgumentList();
                        if (argumentList != null && argumentList.getArguments().length > 1) {
                            JSExpression viewportList = argumentList.getArguments()[1];
                            if (viewportList instanceof JSArrayLiteralExpression) {
                                final JSExpression[] viewports = ((JSArrayLiteralExpression) viewportList).getExpressions();
                                checkForDuplicateViewports(terraDescribeViewports, viewports, holder);
                                checkForViewportsNotInAscendingOrder(viewportList, viewports, holder);
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * Checks for duplicate viewport values in each {@code Terra.describeViewports} block within a file.
     * <p>
     * Zero and one-length viewports arrays are ignored, so that they won't get highlighted in any case.
     * <p>
     * During validation it takes into account only the terra-supported viewport values.
     */
    private void checkForDuplicateViewports(JSCallExpression terraDescribeViewports, @NotNull JSExpression[] viewports, @NotNull ProblemsHolder holder) {
        if (reportDuplicateViewports && viewports.length > 1) {
            final Set<String> processedViewports = new HashSet<>();
            for (JSExpression viewport : viewports) {
                String vpLiteral = getStringValue(viewport);
                if (isSupportedViewport(vpLiteral)) {
                    if (processedViewports.contains(vpLiteral)) {
                        //The reason the 'describeViewports' function name is highlighted in this case is to provide a clearer way of reporting this problem
                        //when the argument list already has issues reported.
                        //noinspection ConstantConditions
                        holder.registerProblem(getDescribeViewportsFunctionNameElement(terraDescribeViewports),
                            TerraBundle.inspection("duplicate.viewports"),
                            ProblemHighlightType.WARNING);
                        return;
                    }
                    processedViewports.add(vpLiteral);
                }
            }
        }
    }

    /**
     * Validates whether the viewport values in each {@code Terra.describeViewports} block within a file are specified in ascending order by their widths.
     * <p>
     * Zero and one-length viewports arrays are ignored, so that they won't get highlighted by this check.
     * <p>
     * During validation it takes into account only the terra-supported viewport values.
     */
    private void checkForViewportsNotInAscendingOrder(@NotNull JSExpression viewportList, @NotNull JSExpression[] viewports, @NotNull ProblemsHolder holder) {
        if (reportViewportsNotInAscendingOrder) {
            //This check ignores zero and one-length arrays, and arrays containing blank item(s)
            if (viewports.length > 1 && Arrays.stream(viewports).allMatch(TerraWdioPsiUtil::isSupportedViewport)) {
                final List<String> actualViewports = Arrays.stream(viewports).map(JSLiteralExpressionUtil::getStringValue).collect(toList());
                //This check takes advantage of the fact that the supported viewports, when they are listed in ascending order
                //by their widths, are in descending order alphabetically.
                if (!actualViewports.equals(reverseSort(actualViewports))) {
                    holder.registerProblem(viewportList, TerraBundle.inspection("viewports.not.in.ascending.order"), ProblemHighlightType.WARNING);
                }
            }
        }
    }

    private List<String> reverseSort(List<String> list) {
        final List<String> reverseSorted = new ArrayList<>(list);
        reverseSorted.sort(reverseOrder());
        return reverseSorted;
    }
}
