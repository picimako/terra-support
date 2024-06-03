//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElement;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraWdioPsiUtil}.
 */
public class TerraWdioPsiUtilTest extends TerraToolkitTestCase {

    //isSupportedViewport

    public void testShouldNotSupportViewport() {
        assertThat(TerraWdioPsiUtil.isSupportedViewport("somenotsupported")).isFalse();
    }

    public void testShouldNotSupportNullViewport() {
        assertThat(TerraWdioPsiUtil.isSupportedViewport((String) null)).isFalse();
    }

    //getMethodExpressionOf

    public void testShouldReturnMethodExpression() {
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(psiElement.getExpression()).thenReturn(jsCallExpression);
        JSExpression methodExpression = mock(JSExpression.class);
        when(jsCallExpression.getMethodExpression()).thenReturn(methodExpression);

        assertThat(TerraWdioPsiUtil.getMethodExpressionOf(psiElement)).isSameAs(methodExpression);
    }

    public void testShouldNotReturnMethodExpressionWhenThereIsNoUnderlyingJSCallExpression() {
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        when(psiElement.getExpression()).thenReturn(null);

        assertThat(TerraWdioPsiUtil.getMethodExpressionOf(psiElement)).isNull();
    }

    //isAnyOfTerraWdioFunctions

    public void testShouldBeATerraWdioFunction() {
        PsiElement psiElement = mock(PsiElement.class);
        PsiElement parent = mock(PsiElement.class);
        when(psiElement.getParent()).thenReturn(parent);
        when(parent.getText()).thenReturn("Terra.validates.element");

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isTrue();
    }

    public void testShouldNotBeATerraWdioFunction() {
        PsiElement psiElement = mock(PsiElement.class);
        PsiElement parent = mock(PsiElement.class);
        when(psiElement.getParent()).thenReturn(parent);
        when(parent.getText()).thenReturn("browser.pause");

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isFalse();
    }

    public void testShouldNotBeATerraWdioFunctionIfThereIsNoParent() {
        PsiElement psiElement = mock(PsiElement.class);

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isFalse();
    }

    //hasText

    public void testShouldHaveText() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            """
                Terra.describeViewports('viewports', ['huge'], () => {
                    describe('terra screenshot', () => {
                       Terra.validates.el<caret>ement('collect');
                    });\
                });""");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent().getParent();

        assertThat(TerraWdioPsiUtil.hasText(element, "Terra.validates.element", "Terra.validates.screenshot")).isTrue();
    }

    public void testShouldNotHaveTextWhenThereNoDesiredTextIsMatched() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            """
                Terra.describeViewports('viewports', ['huge'], () => {
                    describe('terra screenshot', () => {
                       browser.pau<caret>se(2000);
                    });\
                });""");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent().getParent();

        assertThat(TerraWdioPsiUtil.hasText(element, "Terra.validates.element", "Terra.validates.screenshot")).isFalse();
    }

    public void testShouldNotHaveTextWhenNoDesiredTextIsSpecified() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', ['hu<caret>ge'], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(TerraWdioPsiUtil.hasText(element)).isFalse();
    }

    public void testShouldNotHaveTextIfThereIsNoUnderlyingMethodExpression() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', ['hu<caret>ge'], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(TerraWdioPsiUtil.hasText(element, "Terra.validates.screenshot")).isFalse();
    }

    //isScreenshotValidationCall

    public void testShouldBeScreenshotValidationCall() {
        JSCallExpression callExpression = mock(JSCallExpression.class);
        JSExpression methodExpression = mock(JSExpression.class);
        when(callExpression.getMethodExpression()).thenReturn(methodExpression);
        when(methodExpression.getText()).thenReturn("Terra.validates.screenshot");

        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(callExpression)).isTrue();
    }

    public void testShouldNotBeScreenshotValidationCallForNullExpression() {
        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(null)).isFalse();
    }

    public void testShouldNotBeScreenshotValidationCall() {
        JSCallExpression callExpression = mock(JSCallExpression.class);
        JSExpression methodExpression = mock(JSExpression.class);
        when(callExpression.getMethodExpression()).thenReturn(methodExpression);
        when(methodExpression.getText()).thenReturn("Terra.validates.accessibility");

        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(callExpression)).isFalse();
    }
}
