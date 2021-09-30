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

package com.picimako.terra.psi.js;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for working with {@link JSLiteralExpression}s and extracting values from them.
 */
public final class JSLiteralExpressionUtil {

    /**
     * Validates whether the argument element is a String literal expression.
     *
     * @param element the element to check
     * @return true if the element is a String literal expression, false otherwise
     */
    public static boolean isJSStringLiteral(PsiElement element) {
        return element instanceof JSLiteralExpression && ((JSLiteralExpression) element).isStringLiteral();
    }

    /**
     * Gets the String value from the {@link JSLiteralExpression} form of the argument element.
     * <p>
     * Make sure that {@link #isJSStringLiteral(PsiElement)} is run before calling this.
     *
     * @param element the element to query
     * @return the String value of the argument literal expression, or null if there is none
     */
    @Nullable
    public static String getStringValue(PsiElement element) {
        return element instanceof JSLiteralExpression ? ((JSLiteralExpression) element).getStringValue() : null;
    }

    private JSLiteralExpressionUtil() {
        //Utility class
    }
}
