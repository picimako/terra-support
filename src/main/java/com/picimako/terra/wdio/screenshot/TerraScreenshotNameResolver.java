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

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class, based on a Terra screenshot validation call's name argument, resolves the name of the actual screenshot
 * that it references. This logic is based on
 * <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js">visualRegressionConf.js</a>
 * in the terra-toolkit project.
 * <p>
 * The name of the image is resolved using the following pattern:
 * <pre>
 * &lt;parent describe/Terra.describeViewport block's name argument normalized>[&lt;screenshot validation name parameter>].png}
 * </pre>
 * The parent is always the immediate describe or describeViewports block, so in case of a nested structure it is always the most inner
 * one.
 * <p>
 * Since the name of the screenshot doesn't exactly the concatenation of two Strings, but it replaces some characters, this is also
 * handled when building the name of the image. For the list of characters replaced by underscore, see {@link #CHARACTERS_TO_REPLACE}.
 * <p>
 * The name resolution will not happen when the parent describe or describeViewports block's name parameter is missing.
 * <p>
 * There is a special logic to parse test ids from the validation calls' name parameter. In case of a test like
 * <pre>
 * describe('terra screenshot', () => {
 *     it('Test case', () => {
 *         Terra.validates.element('this is the [test id]', { selector: '#selector' });
 *     });
 * });
 * </pre>
 * the test id will be {@code test id}, while the built screenshot file name will be {@code terra_screenshot[test id].png},
 * so not the full parameter value is used.
 * <p>
 * If there are multiple sections of the name parameter enclosed with [ and ], it is always the widest match that will be
 * put into the resolved screenshot name.
 * <p>
 * However if the test id contains at least one ) character like, or the name parameter doesn't even have a portion enclosed
 * by [ and ], the default logic takes place:
 * <pre>
 * describe('terra screenshot', () => {
 *     it('Test case', () => {
 *         Terra.validates.element('this is the [test ) id]', { selector: '#selector' });
 *     });
 * });
 * </pre>
 * the final screenshot name will be {@code terra_screenshot_[this_is_the_[test_)_id]].png}.
 */
public class TerraScreenshotNameResolver {

    /**
     * The regular expression for the character replacement can be found in
     * <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js">terra-toolkit-boneyard//visualRegressionConf.js</a>
     */
    private static final String DELIMITERS_TO_REPLACE = "\\s+|\\.|\\+";
    private static final String CHARACTERS_TO_REPLACE = "[?<>/|*:+\"]";
    private static final Pattern TEST_ID_PATTERN = Pattern.compile("\\[(?<testId>[^)]+)]");
    private static final DescribeOrViewportsBlockCondition DESCRIBE_OR_VIEWPORTS_BLOCK_CONDITION = new DescribeOrViewportsBlockCondition();
    public static final String DESCRIBE_BLOCK_PATTERN = "describe|describe\\.only|describe\\.skip";

    /**
     * Resolves the name of the screenshot referenced by the argument JS literal expression. The argument element must be the first, name parameter
     * of one of the screenshot validation calls:
     * <ul>
     *     <li>Terra.validates.screenshot</li>
     *     <li>Terra.validates.element</li>
     *     <li>Terra.it.matchesScreenshot</li>
     *     <li>Terra.it.validatesElement</li>
     * </ul>
     * Name resolution with this method cannot happen when the parent {@code describe} or {@code Terra.describeViewports} block's name is missing.
     *
     * @param element the JS literal expression on which the resolution takes place
     * @return the resolved image name, or an empty string if the resolution couldn't happen
     */
    @NotNull
    public String resolveName(JSLiteralExpression element) {
        return resolve(element, JsonPsiUtil.stripQuotes(element.getText()));
    }

    /**
     * Resolves the name of the screenshot referenced by the argument method expression representing the method name part of
     * of one of the screenshot validation calls.
     * <p>
     * This method is designed for the case when there is no name parameter specified in the screenshot validation calls,
     * so that the terra library defaults to the value {@code default} for that part of the name.
     *
     * @param methodExpression the method expression on which the resolution takes place
     * @return the resolved image name, or an empty string if the resolution couldn't happen
     */
    @NotNull
    public String resolveDefaultName(JSExpression methodExpression) {
        return resolve(methodExpression, "default");
    }

    /**
     * If the first name argument is specified, then it resolves the screenshot name based on that, otherwise resolves
     * the name by the method expression of the same JS call.
     *
     * @param firstNameArgument the first, name parameter of a validation call
     * @param methodExpression  the method expression of the validation call
     * @return the resolved name
     */
    @NotNull
    public String resolveWithFallback(@Nullable JSLiteralExpression firstNameArgument, JSExpression methodExpression) {
        return firstNameArgument != null ? resolveName(firstNameArgument) : resolveDefaultName(methodExpression);
    }

    private String resolve(PsiElement element, String partialName) {
        PsiElement parentDescribeCall = PsiTreeUtil.findFirstParent(element, DESCRIBE_OR_VIEWPORTS_BLOCK_CONDITION);
        if (parentDescribeCall != null) {
            String describeBlockName = JSPsiUtil.getFirstArgumentAsString(((JSCallExpression) parentDescribeCall).getArgumentList());
            if (describeBlockName != null) {
                return normalize(describeBlockName.trim() + "[" + parseTestId(partialName).trim() + "]") + ".png";
            }
        }
        return "";
    }

    private String parseTestId(String partialName) {
        Matcher testIdMatcher = TEST_ID_PATTERN.matcher(partialName);
        return testIdMatcher.find() ? testIdMatcher.group("testId") : partialName;
    }

    /**
     * Removes and replaces necessary characters in the argument text, so that the screenshot file name can be built properly.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    private String normalize(String text) {
        return text.replaceAll(DELIMITERS_TO_REPLACE, "_").replaceAll(CHARACTERS_TO_REPLACE, "-");
    }

    /**
     * Describes the condition to find the closest {@code describe} or {@code Terra.describeViewports} parent element
     * for a Psi element.
     */
    private static final class DescribeOrViewportsBlockCondition implements Condition<PsiElement> {
        @Override
        public boolean value(PsiElement psiElement) {
            if (psiElement instanceof JSCallExpression) {
                JSExpression methodExpression = ((JSCallExpression) psiElement).getMethodExpression();
                return methodExpression != null
                    && (methodExpression.getText().matches(DESCRIBE_BLOCK_PATTERN) || TERRA_DESCRIBE_VIEWPORTS.equals(methodExpression.getText()));
            }
            return false;
        }
    }
}
