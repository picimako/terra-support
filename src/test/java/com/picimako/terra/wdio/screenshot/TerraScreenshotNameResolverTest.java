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
        myFixture.configureByFile("tests/wdio/nameResolution/resolveName-spec.js");
        JSLiteralExpression element = (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveNameToEmptyStringIfNoParentDescribeCall() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveNameNoParentDescribe-spec.js");

        JSLiteralExpression element = (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    public void testResolveNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveNameNoParentDescribeName-spec.js");

        JSLiteralExpression element = (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveName(element)).isEmpty();
    }

    //resolveDefaultName

    public void testResolveDefaultName() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveDefaultName-spec.js");
        JSExpression element = (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("terra_screenshot[default].png");
    }

    public void testResolveDefaultNameToEmptyStringIfNoParentDescribeCall() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveDefaultNameNoParentDescribe-spec.js");

        JSExpression element = (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("");
    }

    public void testResolveDefaultNameToEmptyStringIfParentDescribeCallHasNoNameParameter() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveDefaultNameNoParentDescribeName-spec.js");

        JSExpression element = (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveDefaultName(element)).isEqualTo("");
    }

    //resolveWithFallback

    public void testResolveByLiteralNoFallback() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveName-spec.js");
        JSLiteralExpression element = (JSLiteralExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveWithFallback(element, null)).isEqualTo("terra_screenshot[with_name].png");
    }

    public void testResolveByMethodExpressionFallback() {
        myFixture.configureByFile("tests/wdio/nameResolution/resolveDefaultName-spec.js");

        JSExpression element = (JSExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(new TerraScreenshotNameResolver().resolveWithFallback(null, element)).isEqualTo("terra_screenshot[default].png");
    }
}
