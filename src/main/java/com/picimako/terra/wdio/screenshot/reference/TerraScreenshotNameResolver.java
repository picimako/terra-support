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

package com.picimako.terra.wdio.screenshot.reference;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * This class, based on a Terra screenshot validation call's name argument, resolves the name of the actual screenshot
 * that it references.
 * <p>
 * The name of the image is resolved using the following pattern:
 * <pre>
 * &lt;parent describe/Terra.describeViewport block's name argument normalized>[&lt;screenshot validation call name parameter>].png}
 * </pre>
 * The parent is always the immediate describe or describeViewports block, so in case of a nested structure it is always the most inner
 * one.
 * <p>
 * Since the name of the screenshot doesn't exactly the concatenation of two Strings, but it replace some characters, this is also
 * handled when building the name of the image. For the list of characters replace by underscore see {@link #CHARACTERS_TO_REPLACE}.
 * <p>
 * The name resolution will not happen when the parent describe or describeViewports block's name parameter is missing.
 */
public class TerraScreenshotNameResolver {

    /**
     * The regular expression for the character replacement can be found in
     * <a href="https://github.com/cerner/terra-toolkit-boneyard/blob/main/config/wdio/visualRegressionConf.js">terra-toolkit-boneyard//visualRegressionConf.js</a>
     */
    private static final String DELIMITERS_TO_REPLACE = "\\s+|\\.";
    private static final String CHARACTERS_TO_REPLACE = "[?<>/|*:+\"]";

    /**
     * Resolves the name of the screenshot referenced by the argument PSI element. The argument element must be the first, name parameter
     * of one of the screenshot validation calls:
     * <ul>
     *     <li>Terra.validates.screenshot</li>
     *     <li>Terra.validates.element</li>
     *     <li>Terra.it.matchesScreenshot</li>
     *     <li>Terra.it.validatesElement</li>
     * </ul>
     * Name resolution cannot happen when the parent {@code describe} or {@code Terra.describeViewports} block's name is missing.
     *
     * @param element the JS literal expression on which the resolution takes place
     * @return the resolved image name, or an empty string if the resolution couldn't happen
     */
    public String resolveName(PsiElement element) {
        return resolve(element, JsonPsiUtil.stripQuotes(element.getText()));
    }

    public String resolveDefaultName(PsiElement element) {
        return resolve(element, "default");
    }

    private String resolve(PsiElement element, String partialName) {
        PsiElement parentDescribeCall = PsiTreeUtil.findFirstParent(element, new DescribeOrViewportsBlockCondition());
        if (parentDescribeCall != null) {
            String describeBlockName = JSPsiUtil.getFirstArgumentAsString(((JSCallExpression) parentDescribeCall).getArgumentList());
            if (describeBlockName != null) {
                return normalize(describeBlockName + "[" + partialName + "]") + ".png";
            }
        }
        return "";
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
                    && ("describe".equals(methodExpression.getText()) || TERRA_DESCRIBE_VIEWPORTS.equals(methodExpression.getText()));
            }
            return false;
        }
    }
}
