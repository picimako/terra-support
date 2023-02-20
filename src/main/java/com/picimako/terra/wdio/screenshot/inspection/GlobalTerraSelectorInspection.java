//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getStringLiteralValue;
import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerra;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.SELECTOR;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getScreenshotValidationProperty;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraElementOrScreenshotValidation;

import java.util.Objects;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Validates whether a CSS selector defined in {@code Terra.validates} and {@code Terra.it} calls match the global selector
 * defined in the {@code wdio.conf.js} located in the project root directory.
 * <p>
 * If they match, the selector property can be removed from the tests, and can rely on the global selector value.
 * <p>
 * Related documentation for {@code TerraService}:
 * <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md#configuration-setup">
 * Wdio Utility Configuration Setup</a>
 *
 * @since 0.2.0
 */
public class GlobalTerraSelectorInspection extends TerraWdioInspectionBase {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerra(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isTerraElementOrScreenshotValidation(node)) {
                    var selectorProperty = getScreenshotValidationProperty(node, SELECTOR);
                    if (selectorProperty != null) {
                        String localScreenshotSelector = getStringLiteralValue(selectorProperty.getValue());
                        String globalSelector = GlobalTerraSelectorRetriever.getInstance(holder.getProject()).getSelector();
                        if (Objects.equals(localScreenshotSelector, globalSelector)) {
                            //At this point selectorProperty.getValue() has already been validated for null value
                            holder.registerProblem(selectorProperty.getValue(), TerraBundle.inspection("matches.global.selector"));
                        }
                    }
                }
            }
        };
    }
}
