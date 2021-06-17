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

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getCallExpression;

import java.util.Optional;
import java.util.function.Consumer;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArgumentsHolder;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides methods for extracting function call arguments.
 */
public final class JSArgumentUtil {

    /**
     * Gets the argument list of the provided statement.
     *
     * @param element the element to get the argument list of
     * @return the argument list, or null if there is none
     */
    @Nullable
    public static JSArgumentList getArgumentListOf(JSExpressionStatement element) {
        JSCallExpression jsCallExpression = getCallExpression(element);
        return jsCallExpression != null ? jsCallExpression.getArgumentList() : null;
    }

    /**
     * Gets the arguments of the provided statement.
     * <p>
     * If there is no argument or argument list, an empty array is returned.
     *
     * @param element the element to get the arguments of
     * @return the array of elements, or empty array if none exist
     */
    @NotNull
    public static JSExpression[] getArgumentsOf(JSExpressionStatement element) {
        return Optional.ofNullable(getArgumentListOf(element)).map(JSArgumentsHolder::getArguments).orElse(JSExpression.EMPTY_ARRAY);
    }

    /**
     * Executes the logic provided in the consumer for the argument list of the psi element.
     *
     * @param element              the element to get the arguments of
     * @param argumentListConsumer the logic to execute on the argument list
     */
    public static void doWithinArgumentListOf(JSExpressionStatement element, Consumer<JSArgumentList> argumentListConsumer) {
        JSCallExpression jsCallExpression = getCallExpression(element);
        if (jsCallExpression != null) {
            JSArgumentList argumentList = jsCallExpression.getArgumentList();
            if (argumentList != null) {
                argumentListConsumer.accept(argumentList);
            }
        }
    }

    /**
     * Returns the value of the argument JS property if it is a numeric literal.
     *
     * @param property to property to get the value of
     * @return the numeric value, or null if the property's value is not a literal, or it is not a numeric literal
     */
    @Nullable
    public static Object getNumericValueOf(JSProperty property) {
        if (property.getValue() instanceof JSLiteralExpression) {
            var literal = (JSLiteralExpression) property.getValue();
            if (literal.isNumericLiteral()) {
                return literal.getValue();
            }
        }
        return null;
    }

    private JSArgumentUtil() {
        //Utility class
    }
}
