//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_TESTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_VALIDATES_SCREENSHOT;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getMethodExpressionOf;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.hasText;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isTopLevelExpression;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for Terra wdio specific inspection implementations, providing some common properties and methods.
 */
public abstract class TerraWdioInspectionBase extends LocalInspectionTool {

    /**
     * Checks whether the argument element corresponds to a top-level {@code Terra.describeViewports} block.
     *
     * @param element the Psi element to check
     * @return true if the argument element is a top-level {@code Terra.describeViewports} block, false otherwise
     */
    protected boolean isTopLevelTerraDescribeViewportsBlock(@NotNull PsiElement element) {
        return isTopLevelExpression(element) && isTerraDescribeViewportsBlock(element);
    }

    /**
     * Checks whether the argument element corresponds to a top-level {@code Terra.describeTests} block.
     *
     * @param element the Psi element to check
     * @return true if the argument element is a top-level {@code Terra.describeTests} block, false otherwise
     */
    protected boolean isTopLevelTerraDescribeTestsBlock(@NotNull PsiElement element) {
        return isTopLevelExpression(element) && isTerraDescribeTestsBlock(element);
    }

    /**
     * Gets whether the argument element is a nested {@code Terra.describeViewports} or {@code Terra.describeTests}
     * block at any level deep.
     * <p>
     * Nested-ness is validated by checking if the argument element's parent element is not the file itself.
     *
     * @param element the Psi element to check
     * @return true if the argument element is a nested {@code Terra.describeViewports} or {@code Terra.describeTests} block, false otherwise
     */
    protected boolean isNestedTerraDescribeHelper(@NotNull PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_DESCRIBE_VIEWPORTS, TERRA_DESCRIBE_TESTS);
    }

    /**
     * Gets whether the argument element is a {@code Terra.validates.screenshot()} call at any level deep.
     * <p>
     * Such calls at top level are ignored.
     *
     * @param element the Psi element to check
     * @return true if the argument is a non-top-level {@code Terra.validates.screenshot()}, false otherwise
     */
    protected boolean isTerraValidatesScreenshotExpression(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_VALIDATES_SCREENSHOT);
    }

    /**
     * Returns the reference name element from the argument expression.
     * <p>
     * In practice that means it returns the element corresponding to the {@code describeViewports} name part of the
     * {@code Terra.describeViewports} expression, or the {@code describeTests} part of {@code Terra.describeTests}.
     * <p>
     * At this state the queried MethodExpression should not be null due to the preliminary check in
     * {@link #isTopLevelTerraDescribeViewportsBlock(PsiElement)}.
     *
     * @param terraDescribeHelper the Terra.describeViewports or Terra.describeTests expression
     * @return the element corresponding to the describeViewports name
     */
    @Nullable
    protected PsiElement getDescribeHelperFunctionNameElement(JSCallExpression terraDescribeHelper) {
        return getFunctionNameElement(terraDescribeHelper.getMethodExpression());
    }

    /**
     * Returns the reference name element from the argument expression.
     * <p>
     * In practice that means it returns the element corresponding to e.g. the {@code matchesScreenshot} name part of the
     * {@code Terra.it.matchesScreenshot} expression.
     *
     * @param element the Psi element to get the name element of
     * @return the element corresponding to the function name
     */
    @Nullable
    protected PsiElement getTerraValidationFunctionNameElement(JSExpressionStatement element) {
        return getFunctionNameElement(getMethodExpressionOf(element));
    }

    /**
     * Returns the reference name element from the argument expression.
     * <p>
     * In practice that means it will return a PsiElement according to the following code snippets:
     * <pre>
     * Terra.describeViewports -> describeViewports
     * Terra.it.matchesScreenshot -> matchesScreenshot
     * Terra.validates.screenshot -> screenshot
     * </pre>
     *
     * @param methodExpression the expression to get the name element of
     * @return the element corresponding to the function name
     */
    @Nullable
    protected PsiElement getFunctionNameElement(JSExpression methodExpression) {
        return ((JSReferenceExpression) methodExpression).getReferenceNameElement();
    }

    private boolean isTerraDescribeViewportsBlock(@NotNull PsiElement element) {
        return hasText(element, TERRA_DESCRIBE_VIEWPORTS);
    }

    private boolean isTerraDescribeTestsBlock(@NotNull PsiElement element) {
        return hasText(element, TERRA_DESCRIBE_TESTS);
    }
}
