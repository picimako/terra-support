//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot.inspection;

import static com.picimako.terra.FileTypePreconditions.isWdioSpecFile;
import static com.picimako.terra.wdio.TerraResourceManager.isUsingTerraFunctionalTesting;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isInContextOfNonTerraItElementOrScreenshotValidation;

import java.util.HashMap;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;
import com.picimako.terra.wdio.TerraWdioInspectionBase;

/**
 * Reports duplicate screenshot names when using terra-functional-testing since more than the same screenshot
 * <p>
 * This inspection applies only to terra-functional-testing, so it is not enforced for terra-toolkit here either.
 * <p>
 * See the last part of
 * <a href="https://engineering.cerner.com/terra-ui/dev_tools/cerner-terra-toolkit-docs/terra-functional-testing/upgrade-guides/version-1-upgrade-guide#upgrading-tests">Terra Upgrade guide/Upgrading Tests</a>.
 *
 * @since 0.6.0
 */
public class DuplicateScreenshotNameInspection extends TerraWdioInspectionBase {

    /**
     * The inspection logic approaches the checks from the literal expressions' direction: instead of going through each JSExpressionStatement in the file,
     * it iterates through the literal expressions, based on the presumption that there are probably less literal expressions in a spec file than expression statements,
     * thus executing less precondition checks.
     * <p>
     * Only the first two matches are registered to make the inspection a bit more performant. Also I don't anticipate it is often to have more than two validations referencing the same screenshot.
     */
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (!isUsingTerraFunctionalTesting(holder.getProject()) || !isWdioSpecFile(session.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JSElementVisitor() {
            @Override
            public void visitJSFile(@NotNull JSFile file) {
                final var duplicateNames = new HashMap<String, JSExpression>();
                PsiTreeUtil.processElements(session.getFile(), JSLiteralExpression.class, element -> {
                    //The check and reporting is applied only to Terra.validates.element and Terra.validates.screenshot calls,
                    //so there is no unnecessary execution for Terra.it calls which are removed from terra-functional-testing anyway.
                    if (element.isStringLiteral() && isInContextOfNonTerraItElementOrScreenshotValidation(element)) {
                        if (duplicateNames.containsKey(element.getStringValue())) {
                            holder.registerProblem(duplicateNames.get(element.getStringValue()), TerraBundle.inspection("duplicate.screenshot.name"));
                            holder.registerProblem(element, TerraBundle.inspection("duplicate.screenshot.name"));
                            return false;
                        }
                        duplicateNames.put(element.getStringValue(), element);
                    }
                    return true;
                });
            }
        };
    }
}
