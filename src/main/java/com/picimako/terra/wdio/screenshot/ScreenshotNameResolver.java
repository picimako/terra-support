/*
 * Copyright 2021 Tamás Balog
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

package com.picimako.terra.wdio.screenshot;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Based on a Terra screenshot validation call's name argument, it resolves the name of the actual screenshot it references.
 */
public interface ScreenshotNameResolver {

    /**
     * Resolves the name of the screenshot referenced by the argument JS literal expression. The argument element must be the first, name parameter
     * of one of the screenshot validation calls:
     * <ul>
     *     <li>Terra.validates.screenshot</li>
     *     <li>Terra.validates.element</li>
     * </ul>
     *
     * @param element the JS literal expression on which the resolution takes place
     * @return the resolved image name, or an empty string if the resolution couldn't happen
     */
    @Nullable
    String resolveName(JSLiteralExpression element);

    /**
     * Resolves the name of the screenshot referenced by the argument method expression representing the method name part of
     * of one of the screenshot validation calls.
     * <p>
     * This method is designed for the case when there is no name parameter specified in the screenshot validation calls,
     * so that the terra library defaults to the value {@code default} for that part of the name.
     * <p>
     * This is applicable only to terra-toolkit based tests. This method is not supported in case of terra-functional-testing.
     *
     * @param methodExpression the method expression on which the resolution takes place
     * @return the resolved image name, or an empty string if the resolution couldn't happen
     */
    @Nullable
    String resolveDefaultName(JSExpression methodExpression);

    /**
     * If the first name argument is specified, then it resolves the screenshot name based on that, otherwise resolves
     * the name by the method expression of the same JS call.
     *
     * @param firstNameArgument the first, name parameter of a validation call
     * @param methodExpression  the method expression of the validation call
     * @return the resolved name
     */
    @Nullable
    String resolveWithFallback(@Nullable JSLiteralExpression firstNameArgument, JSExpression methodExpression);
}
