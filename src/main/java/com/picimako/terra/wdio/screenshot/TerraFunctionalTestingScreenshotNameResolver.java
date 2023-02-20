//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Terra-functional-testing specific implementation of name resolution.
 * <p>
 * This logic is based on
 * <a href="https://github.com/cerner/terra-toolkit/blob/main/packages/terra-functional-testing/src/services/wdio-visual-regression-service/methods/BaseCompare.js">BaseCompare.js</a>.
 * <p>
 * The name of the image is resolved using the name parameter of Terra.validates calls.
 * <p>
 * In the final names of screenshots some characters are replaced, which is also handled when building the name of the image.
 * For the list of characters replaced by underscore, see {@link #CHARACTERS_TO_REPLACE}.
 * <p>
 * There is a special logic to parse test ids from the validation calls' name parameter. In case of a test like
 * <pre>
 * describe('terra screenshot', () => {
 *     it('Test case', () => {
 *         Terra.validates.element('this is the [test id]', { selector: '#selector' });
 *     });
 * });
 * </pre>
 * the test id will be {@code test id}, while the built screenshot file name will be {@code test id.png},
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
 * the final screenshot name will be {@code this_is_the_[test_)_id].png}.
 *
 * @since 0.6.0
 * @see com.picimako.terra.wdio.TerraFunctionalTestingManager
 */
public class TerraFunctionalTestingScreenshotNameResolver extends AbstractScreenshotNameResolver {
    public static final TerraFunctionalTestingScreenshotNameResolver INSTANCE = new TerraFunctionalTestingScreenshotNameResolver();

    /**
     * {@inheritDoc}
     * <p>
     * If the argument element is null, then the returned name is an empty string. This distinction makes sure that
     * no logic is broken due to Terra.validates calls with no name parameters.
     */
    @Override
    @NotNull
    public String resolveName(JSLiteralExpression element) {
        if (element != null) {
            String resolved = normalize(parseTestId(JsonPsiUtil.stripQuotes(element.getText())));
            if (!resolved.isEmpty()) {
                return resolved + ".png";
            }
        }
        return "";

    }

    @Override
    @Nullable
    public String resolveDefaultName(JSExpression methodExpression) {
        return null;
    }

    @Override
    @NotNull
    public String resolveWithFallback(@Nullable JSLiteralExpression firstNameArgument, JSExpression methodExpression) {
        return resolveName(firstNameArgument);
    }
}
