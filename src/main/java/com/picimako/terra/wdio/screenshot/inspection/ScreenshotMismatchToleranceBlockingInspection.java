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

import static com.picimako.terra.FileTypePreconditions.isInWdioSpecFile;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.MISMATCH_TOLERANCE;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSPrefixExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Reports problems regarding the {@code misMatchTolerance} property of screenshot validation function parameters.
 * <p>
 * The validations in this inspection all report problems that would block test execution.
 *
 * @since 0.1.0
 */
public class ScreenshotMismatchToleranceBlockingInspection extends TerraWdioInspectionBase {

    private static final String NEGATIVE_OPERATION_SIGN = "JS:MINUS";

    @SuppressWarnings("PublicField")
    @VisibleForTesting
    public boolean reportMismatchToleranceOutsideOfBoundaries = true;
    @SuppressWarnings("PublicField")
    @VisibleForTesting
    public boolean reportMismatchToleranceIsNonNumeric = true;

    @Override
    @NotNull
    public String getShortName() {
        return "ScreenshotMismatchToleranceBlocking";
    }

//    @Override
//    public JComponent createOptionsPanel() {
//        final MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
//        panel.addCheckbox("Report values outside of boundaries (outside of 0-100)", "reportMismatchToleranceOutsideOfBoundaries");
//        panel.addCheckbox("Report non-numeric values", "reportMismatchToleranceIsNonNumeric");
//        return panel;
//    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTerraElementOrScreenshotValidationFunction(node)) {
                    JSProperty misMatchToleranceProperty = getScreenshotValidationProperty(node, MISMATCH_TOLERANCE);
                    if (misMatchToleranceProperty != null) {
                        checkForMismatchToleranceOutsideOfBoundaries(misMatchToleranceProperty, reportMismatchToleranceOutsideOfBoundaries, holder);
                        if (misMatchToleranceProperty.getValue() instanceof JSLiteralExpression) {
                            JSLiteralExpression literal = (JSLiteralExpression) misMatchToleranceProperty.getValue();
                            if (!literal.isNumericLiteral()) {
                                registerProblemForNonNumericPropertyValue(literal, holder);
                            }
                        } else {
                            registerProblemForNonNumericPropertyValue(misMatchToleranceProperty.getValue(), holder);
                        }
                    }
                }
            }
        };
    }

    private void registerProblemForNonNumericPropertyValue(JSExpression propertyValue, @NotNull ProblemsHolder holder) {
        if (reportMismatchToleranceIsNonNumeric) {
            holder.registerProblem(propertyValue, TerraBundle.inspection("mismatch.tolerance.not.numeric.value"));
        }
    }

    /**
     * Validates if the argument property's value is between 0 and 100, and registers a problem if it's not.
     * <p>
     * Double, Integer, Long and Float values are checked, others are ignored during the validation.
     * <p>
     * Negative values are not stored as {@code JSLiteralExpression}s in the PSI tree, rather wrapped in a
     * {@code JSPrefixExpression} along with its operation sign.
     *
     * @param property the property to check
     */
    private void checkForMismatchToleranceOutsideOfBoundaries(JSProperty property, boolean reportProblem, ProblemsHolder holder) {
        if (reportProblem) {
            JSExpression literal = property.getValue();
            if (literal instanceof JSLiteralExpression) {
                if (isGreaterThanMaxAllowedValue(((JSLiteralExpression) literal).getValue())) {
                    holder.registerProblem(literal, TerraBundle.inspection("mismatch.tolerance.outside.of.range"));
                }
            } else if (literal instanceof JSPrefixExpression) {
                IElementType operationSign = ((JSPrefixExpression) literal).getOperationSign();
                if (operationSign != null && NEGATIVE_OPERATION_SIGN.equals(operationSign.toString())) {
                    holder.registerProblem(literal, TerraBundle.inspection("mismatch.tolerance.outside.of.range"));
                }
            }
        }
    }

    private boolean isGreaterThanMaxAllowedValue(Object literalValue) {
        return literalValue instanceof Double && (Double) literalValue > 100 || literalValue instanceof Float && (Float) literalValue > 100
            || literalValue instanceof Integer && (Integer) literalValue > 100 || literalValue instanceof Long && (Long) literalValue > 100;
    }
}
