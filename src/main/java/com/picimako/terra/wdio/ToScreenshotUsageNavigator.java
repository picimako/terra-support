//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.wdio.TerraWdioPsiUtil.isScreenshotValidationCall;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import com.picimako.terra.wdio.screenshot.ScreenshotNameResolver;

/**
 * Provides functionality to navigate from a screenshot, or a UI item representing a screenshot, to the usage of
 * that image in a Terra wdio spec file.
 * <p>
 * When the navigation happens, the caret is positioned either on the Terra calls line (default screenshot), or the
 * validation's name parameter (non-default screenshot).
 */
public class ToScreenshotUsageNavigator {

    private final ScreenshotNameResolver resolver;
    private boolean hasNavigated;

    public ToScreenshotUsageNavigator(Project project) {
        resolver = TerraResourceManager.getInstance(project).screenshotNameResolver();
    }

    /**
     * Navigates to the Terra validation call in the given spec file, based on the provided screenshot name.
     *
     * @param specFile       the spec file to lookup the Terra validation call in
     * @param screenshotName the screenshot name with extension, by which the validation calls in looked up
     * @return true if navigation to the Terra validation was successful, false otherwise
     */
    public boolean navigateToUsage(PsiFile specFile, String screenshotName) {
        PsiTreeUtil.processElements(specFile, JSCallExpression.class, element -> !hasNavigatedToUsage(screenshotName, element));
        return hasNavigated;
    }

    private boolean hasNavigatedToUsage(String selectedScreenshotName, JSCallExpression callExpression) {
        hasNavigated = false;
        if (isScreenshotValidationCall(callExpression)) {
            JSLiteralExpression nameExpr = getFirstArgumentAsStringLiteral(callExpression.getArgumentList());
            String resolvedScreenshotName = resolver.resolveWithFallback(nameExpr, callExpression.getMethodExpression());

            if (selectedScreenshotName.equals(resolvedScreenshotName)) {
                if (nameExpr != null) {
                    nameExpr.navigate(true);
                } else {
                    //If the method expression was null, the execution wouldn't reach this point. That null value is
                    // handled by the IntelliJ platform via the resolver.resolveWithFallback() call.
                    callExpression.getMethodExpression().navigate(true);
                }
                hasNavigated = true;
            }
        }
        return hasNavigated;
    }
}
