//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
