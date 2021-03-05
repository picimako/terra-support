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

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link TerraScreenshotNameResolver}.
 */
public class TerraScreenshotNameResolverTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    //resolveName

    public void testResolveName() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveNameToEmptyStringIfNoParentDescribeCall() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    public void testResolveNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    //resolveName with partial test id

    public void testDefaultScreenshotPartialNameWithTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveDefaultNameWithTestId-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolvePartialNameWithTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveNameWithTestId-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[test_id].png");
    }

    public void testResolvePartialNameWithMultipleTestIds() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveNameWithMultipleTestIds-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[test_id]_and_another_[testid].png");
    }

    public void testResolveFullNameWithNonMatchingPartialTestId() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveNameWithNonMatchingTestId-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[with_name_[tes)t_id]].png");
    }

    //resolveDefaultName

    public void testResolveDefaultName() {
        JSExpression element = configureFileForJSExpression("tests/wdio/nameResolution/resolveDefaultName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolveDefaultNameForNonDefaultValidation() {
        JSExpression element = configureFileForJSExpression("tests/wdio/nameResolution/resolveDefaultNameForNonDefaultValidation-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolveDefaultNameToEmptyStringIfNoParentDescribeCall() {
        JSExpression element = configureFileForJSExpression("tests/wdio/nameResolution/resolveDefaultNameNoParentDescribe-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEmpty();
    }

    public void testResolveDefaultNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        JSExpression element = configureFileForJSExpression("tests/wdio/nameResolution/resolveDefaultNameNoParentDescribeName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEmpty();
    }

    //resolveWithFallback

    public void testResolveByLiteralNoFallback() {
        JSLiteralExpression element = configureFileForJSLiteralExpression("tests/wdio/nameResolution/resolveName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveWithFallback(element, null)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveByMethodExpressionFallback() {
        JSExpression element = configureFileForJSExpression("tests/wdio/nameResolution/resolveDefaultName-spec.js");
        assertThat(new TerraScreenshotNameResolver().resolveWithFallback(null, element)).isEqualTo("terra_screenshot[default].png");
    }

    //Helper methods

    private JSExpression configureFileForJSExpression(String path) {
        myFixture.configureByFile(path);
        return (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }

    private JSLiteralExpression configureFileForJSLiteralExpression(String path) {
        myFixture.configureByFile(path);
        return (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }
}
