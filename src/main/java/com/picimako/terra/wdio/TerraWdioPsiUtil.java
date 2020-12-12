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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getCallExpression;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.getStringValue;
import static com.picimako.terra.psi.js.JSLiteralExpressionUtil.isJSStringLiteral;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides utility methods and properties for Terra WDIO test related features.
 */
public final class TerraWdioPsiUtil {

    public static final String TERRA_DESCRIBE_VIEWPORTS = "Terra.describeViewports";
    public static final String TERRA_VALIDATES_SCREENSHOT = "Terra.validates.screenshot";
    public static final String TERRA_VALIDATES_ELEMENT = "Terra.validates.element";
    public static final String TERRA_IT_MATCHES_SCREENSHOT = "Terra.it.matchesScreenshot";
    public static final String TERRA_IT_VALIDATES_ELEMENT = "Terra.it.validatesElement";

    public static final String TERRA_HIDE_INPUT_CARET = "Terra.hideInputCaret";
    public static final String TERRA_VIEWPORTS = "Terra.viewports";
    public static final String TERRA_IT_IS_ACCESSIBLE = "Terra.it.isAccessible";
    public static final String TERRA_VALIDATES_ACCESSIBILITY = "Terra.validates.accessibility";

    public static final String TERRA = "Terra";
    public static final String TERRA_VALIDATES = "Terra.validates";
    public static final String TERRA_IT = "Terra.it";

    private static final Set<String> TERRA_FUNCTIONS = Set.of(TERRA_DESCRIBE_VIEWPORTS, TERRA_VIEWPORTS, TERRA_HIDE_INPUT_CARET,
            TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT, TERRA_IT_IS_ACCESSIBLE,
            TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT, TERRA_VALIDATES_ACCESSIBILITY,
            TERRA, TERRA_VALIDATES, TERRA_IT);

    /**
     * The items in the list are in ascending order by their widths.
     */
    public static final List<String> SUPPORTED_TERRA_VIEWPORTS = List.of("tiny", "small", "medium", "large", "huge", "enormous");

    public static final Set<String> SCREENSHOT_VALIDATION_NAMES = Set.of(TERRA_VALIDATES_SCREENSHOT, TERRA_VALIDATES_ELEMENT,
        TERRA_IT_MATCHES_SCREENSHOT, TERRA_IT_VALIDATES_ELEMENT);


    /**
     * Gets whether the viewport literal String in the argument is supported by terra.
     *
     * @param viewport the JSExpression Psi node of the viewport
     * @return true if the viewport is supported by terra, false otherwise
     */
    public static boolean isSupportedViewport(JSExpression viewport) {
        return isJSStringLiteral(viewport) && isSupportedViewport(getStringValue(viewport));
    }
    /**
     * Gets whether the argument viewport is supported by terra.
     *
     * @param viewport the viewport to check
     * @return true if the viewport is supported by terra, false otherwise
     */
    public static boolean isSupportedViewport(String viewport) {
        return SUPPORTED_TERRA_VIEWPORTS.contains(viewport);
    }

    /**
     * Gets the underlying method expression of the argument element.
     * <p>
     * From that expression further information about the function call identifier can be retrieved.
     *
     * @param element the element to query
     * @return the underlying method expression if found, null otherwise
     */
    @Nullable
    public static JSExpression getMethodExpressionOf(@NotNull PsiElement element) {
        JSCallExpression jsCallExpression = getCallExpression(element);
        return jsCallExpression != null ? jsCallExpression.getMethodExpression() : null;
    }

    /**
     * Gets whether the argument element is one of the Terra wdio objects or functions.
     *
     * @param element the element to check
     * @return true if the element is one of the Terra wdio objects or functions, false otherwise
     */
    public static boolean isAnyOfTerraWdioFunctions(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        return parent != null && TERRA_FUNCTIONS.contains(parent.getText());
    }

    /**
     * Gets whether the element's underlying method expression has any of the text provided in the {@code desiredTexts} argument.
     *
     * @param element the element to check the text of
     * @param desiredTexts the acceptable collection of texts
     * @return true if there is at least one desired text, and the element has text of any of the provided texts, false otherwise
     */
    public static boolean hasText(@NotNull PsiElement element, String... desiredTexts) {
        if (desiredTexts.length > 0) {
            JSExpression methodExpression = getMethodExpressionOf(element);
            return methodExpression != null && Arrays.stream(desiredTexts).anyMatch(text -> text.equals(methodExpression.getText()));
        }
        return false;
    }

    private TerraWdioPsiUtil() {
        //Utility class
    }
}
