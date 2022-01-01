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

package com.picimako.terra.wdio;

import static com.intellij.lang.javascript.buildTools.JSPsiUtil.getFirstArgumentAsStringLiteral;
import static com.picimako.terra.BuildNumberHelper.isIDEBuildNumberSameOrNewerThan;
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
        if (isIDEBuildNumberSameOrNewerThan("202.5103.13")) {
            PsiTreeUtil.processElements(specFile, JSCallExpression.class, element -> !hasNavigatedToUsage(screenshotName, element));
        } else {
            PsiTreeUtil.processElements(specFile, element ->
                !(element instanceof JSCallExpression) || !hasNavigatedToUsage(screenshotName, (JSCallExpression) element));
        }
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
