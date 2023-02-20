//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio.screenshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraToolkitScreenshotNameResolver}.
 */
public class TerraToolkitScreenshotNameResolverTest extends BasePlatformTestCase {

    //resolveName

    public void testResolveName() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with name<caret>', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveNameToEmptyStringIfNoParentDescribeCall() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "{\n" +
            "    Terra.it.matchesScreenshot('with name<caret>', { selector: '#selector' });\n" +
            "}");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    public void testResolveNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe(() => {\n" +
            "        Terra.it.matchesScreenshot('with name<caret>', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    //resolveName with partial test id

    public void testDefaultScreenshotPartialNameWithTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with name <caret>[default]', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolvePartialNameWithTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with name <caret>[test id]', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[test_id].png");
    }

    public void testResolvePartialNameWithMultipleTestIds() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with <caret>[test id] and another [testid]', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[test_id]_and_another_[testid].png");
    }

    public void testResolveFullNameWithNonMatchingPartialTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with name <caret>[tes)t id]', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[with_name_[tes)t_id]].png");
    }

    //resolveDefaultName

    public void testResolveDefaultName() {
        JSExpression element = configureFileForJSExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreen<caret>shot({ selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolveDefaultNameForNonDefaultValidation() {
        JSExpression element = configureFileForJSExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreen<caret>shot('default', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolveDefaultNameToEmptyStringIfNoParentDescribeCall() {
        JSExpression element = configureFileForJSExpression(
            "{\n" +
            "    Terra.it.matchesScreen<caret>shot({ selector: '#selector' });\n" +
            "}\n");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveDefaultName(element)).isEmpty();
    }

    public void testResolveDefaultNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        JSExpression element = configureFileForJSExpression(
            "describe('outer describe', () => {\n" +
            "    describe(() => {\n" +
            "        Terra.it.matchesScreen<caret>shot({ selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveDefaultName(element)).isEmpty();
    }

    //resolveWithFallback

    public void testResolveByLiteralNoFallback() {
        JSLiteralExpression element = configureFileForJSLiteralExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreenshot('with name<caret>', { selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveWithFallback(element, null)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveByMethodExpressionFallback() {
        JSExpression element = configureFileForJSExpression(
            "describe('outer describe', () => {\n" +
            "    describe('terra screenshot', () => {\n" +
            "        Terra.it.matchesScreen<caret>shot({ selector: '#selector' });\n" +
            "    });\n" +
            "});");
        assertThat(new TerraToolkitScreenshotNameResolver().resolveWithFallback(null, element)).isEqualTo("terra_screenshot[default].png");
    }

    //Helper methods

    private JSExpression configureFileForJSExpression(String text) {
        myFixture.configureByText(JavaScriptFileType.INSTANCE, text);
        return (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }

    private JSLiteralExpression configureFileForJSLiteralExpression(String text) {
        myFixture.configureByText(JavaScriptFileType.INSTANCE, text);
        return (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }
}
