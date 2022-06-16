//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.MISMATCH_TOLERANCE;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.MIS_MATCH_TOLERANCE;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getScreenshotValidationProperty;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraElementOrScreenshotValidation;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSPrefixExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Reports problems regarding the {@code misMatchTolerance} property of screenshot validation function parameters.
 *
 * @since 0.1.0
 */
public class ScreenshotMismatchToleranceInspection extends TerraWdioInspectionBase {

    private static final double DEFAULT_THRESHOLD = 0.5;
    private static final String NEGATIVE_OPERATION_SIGN = "JS:MINUS";

    @SuppressWarnings("PublicField")
    @TestOnly
    boolean reportMismatchToleranceOutsideOfBoundaries = true;
    @SuppressWarnings("PublicField")
    @TestOnly
    boolean reportMismatchToleranceIsNonNumeric = true;
    @SuppressWarnings("PublicField")
    public double maxThreshold = DEFAULT_THRESHOLD;

    @Override
    @Nullable
    public JComponent createOptionsPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final JLabel label = new JLabel("Mismatch tolerance max threshold (0 - 100):");
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.right = UIUtil.DEFAULT_HGAP;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(label, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets.right = 0;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(mismatchToleranceValueField(), constraints);
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
                if (!isTerraElementOrScreenshotValidation(node)) return;

                JSProperty misMatchToleranceProperty = getScreenshotValidationProperty(node, MIS_MATCH_TOLERANCE, MISMATCH_TOLERANCE);
                if (misMatchToleranceProperty != null) {
                    checkForMismatchToleranceOutsideOfBoundaries(misMatchToleranceProperty, reportMismatchToleranceOutsideOfBoundaries, holder);
                    if (misMatchToleranceProperty.getValue() instanceof JSLiteralExpression) {
                        JSLiteralExpression literal = (JSLiteralExpression) misMatchToleranceProperty.getValue();
                        if (!literal.isNumericLiteral())
                            registerProblemForNonNumericPropertyValue(literal, holder);
                        else checkForMismatchToleranceAboveThreshold(literal, holder);
                    } else registerProblemForNonNumericPropertyValue(misMatchToleranceProperty.getValue(), holder);
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

    /**
     * Checks for and registers a problem for the argument literal if the underlying mismatch tolerance value is above
     * the allowed threshold.
     * <p>
     * Double, Integer, Long and Float values are checked, others are ignored during the validation.
     *
     * @param literal the literal to check and register
     */
    private void checkForMismatchToleranceAboveThreshold(JSLiteralExpression literal, @NotNull ProblemsHolder holder) {
        if (isMismatchToleranceAboveThreshold(literal.getValue())) {
            holder.registerProblem(literal, String.format(TerraBundle.inspection("mismatch.tolerance.above.threshold"), maxThreshold), ProblemHighlightType.WARNING);
        }
    }

    private boolean isMismatchToleranceAboveThreshold(Object misMatchToleranceValue) {
        return misMatchToleranceValue instanceof Double && ((Double) misMatchToleranceValue) > maxThreshold
            || misMatchToleranceValue instanceof Integer && ((Integer) misMatchToleranceValue) > maxThreshold
            || misMatchToleranceValue instanceof Long && ((Long) misMatchToleranceValue) > maxThreshold
            || misMatchToleranceValue instanceof Float && ((Float) misMatchToleranceValue) > maxThreshold;
    }

    /**
     * Creates and returns a text field that allows only digits and the dot character to be typed into it.
     * <p>
     * If the value specified in the field cannot be converted to a double, or it is a valid double value, but it is
     * less than 0 or greater than 100 (the allowed values for misMatchTolerance), the previously saved value is kept.
     */
    private JTextField mismatchToleranceValueField() {
        final JTextField valueField = new JTextField(String.valueOf(maxThreshold));
        valueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c != '.' && (c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume(); //ignore event
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!valueField.getText().isBlank()) {
                    try {
                        double value = Double.parseDouble(valueField.getText());
                        if (value >= 0 || value <= 100) {
                            maxThreshold = value;
                        }
                    } catch (NumberFormatException ignored) {
                        //no-op if the format is invalid
                    }
                }
            }
        });
        return valueField;
    }
}
