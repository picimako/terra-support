//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
