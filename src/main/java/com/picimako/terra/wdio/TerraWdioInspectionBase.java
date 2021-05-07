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

package com.picimako.terra.wdio;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_IT_MATCHES_SCREENSHOT;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_IT_VALIDATES_ELEMENT;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_VALIDATES_ELEMENT;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_VALIDATES_SCREENSHOT;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.getMethodExpressionOf;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.hasText;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
     * Gets whether the argument element is a nested {@code Terra.describeViewports} block at any level deep.
     * <p>
     * Nested-ness is validated by checking if the argument element's parent element is not the file itself.
     *
     * @param element the Psi element to check
     * @return true if the argument element is a nested {@code Terra.describeViewports} block, false otherwise
     */
    protected boolean isNestedTerraDescribeViewportsBlock(@NotNull PsiElement element) {
        return !isTopLevelExpression(element) && isTerraDescribeViewportsBlock(element);
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
     * Gets whether the argument element is a {@code Terra.it.matchesScreenshot()} call at any level deep.
     * <p>
     * Such calls at top level are ignored.
     *
     * @param element the Psi element to check
     * @return true if the argument is a non-top-level {@code Terra.it.matchesScreenshot()}, false otherwise
     */
    protected boolean isTerraItMatchesScreenshotExpression(PsiElement element) {
        return !isTopLevelExpression(element) && hasText(element, TERRA_IT_MATCHES_SCREENSHOT);
    }

    /**
     * Gets whether the argument element corresponds to one of the Terra function calls that do screenshot validation,
     * namely:
     * <ul>
     *     <li>Terra.validates.screenshot</li>
     *     <li>Terra.validates.element</li>
     *     <li>Terra.it.matchesScreenshot</li>
     *     <li>Terra.it.validatesElement</li>
     * </ul>
     *
     * @param element the element to validate
     * @return true if the argument is one of the Terra screenshot matching functions, false otherwise
     */
    protected boolean isTerraElementOrScreenshotValidationFunction(PsiElement element) {
        return !isTopLevelExpression(element)
                && hasText(element, TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT, TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT);
    }

    /**
     * Returns the reference name element from the argument expression.
     * <p>
     * In practice that means it returns the element corresponding to the {@code describeViewports} name part of the
     * {@code Terra.describerViewports} expression.
     * <p>
     * At this state the queried MethodExpression should not be null due to the preliminary check in
     * {@link #isTopLevelTerraDescribeViewportsBlock(PsiElement)}.
     *
     * @param terraDescribeViewports the Terra.describeViewports expression
     * @return the element corresponding to the describeViewports name
     */
    @Nullable
    protected PsiElement getDescribeViewportsFunctionNameElement(JSCallExpression terraDescribeViewports) {
        return getFunctionNameElement(terraDescribeViewports.getMethodExpression());
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

    private boolean isTopLevelExpression(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiFile;
    }

    private boolean isTerraDescribeViewportsBlock(@NotNull PsiElement element) {
        return hasText(element, TERRA_DESCRIBE_VIEWPORTS);
    }
}
