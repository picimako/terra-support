//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider;

/**
 * Based on a Terra screenshot validation call's name argument, it resolves the name of the actual screenshot it references.
 * 
 * Methods in this interface are not annotated as {@code @Nullable} due to type mismatch in
 * {@link com.picimako.terra.wdio.screenshot.inlayhint.TerraScreenshotInlayHintsProvider#getCollectorFor(PsiFile, Editor, TerraScreenshotInlayHintsProvider.Settings, InlayHintsSink)}. 
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
    String resolveName(JSLiteralExpression element);

    /**
     * Resolves the name of the screenshot referenced by the argument method expression representing the method name part
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
    String resolveDefaultName(JSExpression methodExpression);

    /**
     * If the first name argument is specified, then it resolves the screenshot name based on that, otherwise resolves
     * the name by the method expression of the same JS call.
     *
     * @param firstNameArgument the first, name parameter of a validation call
     * @param methodExpression  the method expression of the validation call
     * @return the resolved name
     */
    String resolveWithFallback(@Nullable JSLiteralExpression firstNameArgument, JSExpression methodExpression);
}
