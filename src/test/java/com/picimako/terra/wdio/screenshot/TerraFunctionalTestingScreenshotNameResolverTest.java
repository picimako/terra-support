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

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraFunctionalTestingScreenshotNameResolver}.
 */
public class TerraFunctionalTestingScreenshotNameResolverTest extends BasePlatformTestCase {

    //resolveName

    public void testResolveName() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('terra screenshot<caret>', { selector: '#selector' });");
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot.png");
    }

    public void testResolveEmptyNameAsEmptyString() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('<caret>', { selector: '#selector' });");
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    public void testResolveMissingNameAsEmptyString() {
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(null)).isEmpty();
    }

    //resolveName with partial test id

    public void testResolvePartialNameWithTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('terra screenshot <caret>[test id]', { selector: '#selector' });");
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(element)).isEqualTo("test_id.png");
    }

    public void testResolvePartialNameWithMultipleTestIds() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('terra screenshot <caret>[test id] and another [testid]', { selector: '#selector' });");
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(element)).isEqualTo("test_id]_and_another_[testid.png");
    }

    public void testResolveFullNameWithNonMatchingPartialTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('terra screenshot <caret>[tes)t id]', { selector: '#selector' });");
        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot_[tes)t_id].png");
    }

    //resolveDefaultName

    public void testThrowsUnsupportedOperationExceptionForResolvingWithDefaultName() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "Terra.validates.element('with name<caret>', { selector: '#selector' });");

        assertThat(new TerraFunctionalTestingScreenshotNameResolver().resolveDefaultName(element)).isNull();
    }

    //Helper methods

    private JSLiteralExpression configureFileForJSLiteralExpression(String text) {
        myFixture.configureByText(JavaScriptFileType.INSTANCE, text);
        return (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }
}
