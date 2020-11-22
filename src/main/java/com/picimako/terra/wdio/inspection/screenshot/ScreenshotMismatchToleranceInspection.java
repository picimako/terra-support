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

package com.picimako.terra.wdio.inspection.screenshot;

import static com.picimako.terra.FileTypePreconditionsUtil.isInWdioSpecFile;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.inspection.TerraWdioInspectionBase;

/**
 * Reports problems regarding the {@code misMatchTolerance} property of screenshot validation function parameters.
 * <p>
 * The validations in this inspection all report problems that would not block test execution.
 */
public class ScreenshotMismatchToleranceInspection extends TerraWdioInspectionBase {

    private static final String MISMATCH_TOLERANCE_ABOVE_THRESHOLD_MESSAGE = "The mismatch tolerance is above the max threshold (%s)";
    private static final String MISMATCH_TOLERANCE = "misMatchTolerance";
    private static final double DEFAULT_THRESHOLD = 0.5;

    @SuppressWarnings("PublicField")
    public double maxThreshold = DEFAULT_THRESHOLD;

    @Override
    @NotNull
    public String getShortName() {
        return "ScreenshotMismatchTolerance";
    }

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
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTerraElementOrScreenshotValidationFunction(node)) {
                    JSProperty misMatchToleranceProperty = getScreenshotValidationProperty(node, MISMATCH_TOLERANCE);
                    if (misMatchToleranceProperty != null && misMatchToleranceProperty.getValue() instanceof JSLiteralExpression) {
                        JSLiteralExpression literal = (JSLiteralExpression) misMatchToleranceProperty.getValue();
                        if (literal.isNumericLiteral()) {
                            checkForMismatchToleranceAboveThreshold(literal, holder);
                        }
                    }
                }
            }
        };
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
            holder.registerProblem(literal, String.format(MISMATCH_TOLERANCE_ABOVE_THRESHOLD_MESSAGE, maxThreshold));
        }
    }

    private boolean isMismatchToleranceAboveThreshold(Object misMatchToleranceValue) {
        return misMatchToleranceValue instanceof Double && ((Double) misMatchToleranceValue) > maxThreshold
                || misMatchToleranceValue instanceof Integer && ((Integer) misMatchToleranceValue) > maxThreshold
                || misMatchToleranceValue instanceof Long && ((Long) misMatchToleranceValue) > maxThreshold
                || misMatchToleranceValue instanceof Float && ((Float) misMatchToleranceValue) > maxThreshold;
    }

    /**
     * Creates a text field that allows only digits and the dot character to be typed into it.
     * <p>
     * If the value specified in the field cannot be converted to a double, or it is a valid double value, but it is
     * less than 0 or greater than 100 (the allowed values for misMatchTolerance), the previously saved value is kept.
     *
     * @return the configured text field
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
                    }
                }
            }
        });
        return valueField;
    }
}
