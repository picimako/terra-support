//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getTerraValidationProperties;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isAccessibilityValidation;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isElementValidation;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidation;

import java.util.List;
import java.util.Optional;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraResourceManager;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * This inspection reports JS properties of {@code Terra.it} and {@code Terra.validates} helpers,
 * that are not valid.
 * <p>
 * terra-toolkit and terra-functional-testing specific properties are handled as well.
 *
 * @see TerraToolkitPropertiesProvider
 * @see TerraFunctionalTestingPropertiesProvider
 * @since 0.6.0
 */
public class InvalidTerraValidationPropertiesInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                TerraPropertiesProvider properties = TerraResourceManager.getInstance(node.getProject()).screenshotValidationProperties();
                if (isScreenshotValidation(node)) checkIncorrectPropertyName(node, properties.screenshotProperties());
                else if (isElementValidation(node)) checkIncorrectPropertyName(node, properties.elementProperties());
                else if (isAccessibilityValidation(node)) checkIncorrectPropertyName(node, properties.accessibilityProperties());
            }

            private void checkIncorrectPropertyName(JSExpressionStatement node, List<String> validPropertyNames) {
                for (var property : getTerraValidationProperties(node)) {
                    if (!validPropertyNames.contains(property.getName())) {
                        holder.registerProblem(Optional.ofNullable(property.getIdentifyingElement()).orElse(property),
                            TerraBundle.inspection("invalid.terra.validation.property", validPropertyNames));
                    }
                }
            }
        };
    }
}
