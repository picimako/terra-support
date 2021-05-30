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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getStringLiteralValue;
import static com.picimako.terra.FileTypePreconditions.isInWdioSpecFile;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.SELECTOR;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getScreenshotValidationProperty;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTerraElementOrScreenshotValidationFunction;

import java.util.Objects;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSProperty;
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
 * <p>
 * TODO: Potential support for `` strings
 *
 * @since 0.2.0
 */
public class GlobalTerraSelectorInspection extends TerraWdioInspectionBase {

    private final GlobalTerraSelectorRetriever globalSelectorRetriever = new GlobalTerraSelectorRetriever();

    @Override
    public @NotNull String getShortName() {
        return "GlobalTerraSelector";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSExpressionStatement(JSExpressionStatement node) {
                super.visitJSExpressionStatement(node);

                if (isInWdioSpecFile(node) && isTerraElementOrScreenshotValidationFunction(node)) {
                    JSProperty selectorProperty = getScreenshotValidationProperty(node, SELECTOR);
                    if (selectorProperty != null) {
                        String screenshotSelector = getStringLiteralValue(selectorProperty.getValue());
                        if (Objects.equals(screenshotSelector, globalSelectorRetriever.getSelector(holder.getProject()))) {
                            //At this point selectorProperty.getValue() has already been validated for null value
                            holder.registerProblem(selectorProperty.getValue(), TerraBundle.inspection("matches.global.selector"));
                        }
                    }
                }
            }
        };
    }
}
