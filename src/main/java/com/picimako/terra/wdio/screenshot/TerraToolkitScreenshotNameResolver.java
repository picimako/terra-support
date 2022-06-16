//Copyright 2020 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import static com.picimako.terra.wdio.TerraWdioPsiUtil.TERRA_DESCRIBE_VIEWPORTS;

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
 * Terra-toolkit specific implementation of name resolution.
 * <p>
 * The resolution logic is based on
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
 *
 * @see com.picimako.terra.wdio.TerraToolkitManager
 */
public class TerraToolkitScreenshotNameResolver extends AbstractScreenshotNameResolver {
    public static final TerraToolkitScreenshotNameResolver INSTANCE = new TerraToolkitScreenshotNameResolver();

    private static final DescribeOrViewportsBlockCondition DESCRIBE_OR_VIEWPORTS_BLOCK_CONDITION = new DescribeOrViewportsBlockCondition();
    public static final String DESCRIBE_BLOCK_PATTERN = "describe|describe\\.only|describe\\.skip";

    /**
     * {@inheritDoc}
     * <p>
     * Additional validation helpers affected:
     * <ul>
     *     <li>Terra.it.matchesScreenshot</li>
     *     <li>Terra.it.validatesElement</li>
     * </ul>
     * <p>
     * Name resolution with this method cannot happen when the parent {@code describe} or {@code Terra.describeViewports} block's name is missing.
     */
    @NotNull
    @Override
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
    @Override
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
    @Override
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
