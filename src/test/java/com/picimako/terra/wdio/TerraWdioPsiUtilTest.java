/*
 * Copyright 2020 Tamás Balog
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.PsiElement;
import org.junit.Test;

/**
 * Unit test for {@link TerraWdioPsiUtil}.
 */
public class TerraWdioPsiUtilTest {

    //isSupportedViewport

    @Test
    public void shouldNotSupportViewport() {
        assertThat(TerraWdioPsiUtil.isSupportedViewport("somenotsupported")).isFalse();
    }

    //getMethodExpressionOf

    @Test
    public void shouldReturnMethodExpression() {
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(psiElement.getExpression()).thenReturn(jsCallExpression);
        JSExpression methodExpression = mock(JSExpression.class);
        when(jsCallExpression.getMethodExpression()).thenReturn(methodExpression);

        assertThat(TerraWdioPsiUtil.getMethodExpressionOf(psiElement)).isSameAs(methodExpression);
    }

    @Test
    public void shouldNotReturnMethodExpressionWhenThereIsNoUnderlyingJSCallExpression() {
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        when(psiElement.getExpression()).thenReturn(null);

        assertThat(TerraWdioPsiUtil.getMethodExpressionOf(psiElement)).isNull();
    }

    //isAnyOfTerraWdioFunctions

    @Test
    public void shouldBeATerraWdioFunction() {
        PsiElement psiElement = mock(PsiElement.class);
        PsiElement parent = mock(PsiElement.class);
        when(psiElement.getParent()).thenReturn(parent);
        when(parent.getText()).thenReturn("Terra.validates.element");

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isTrue();
    }

    @Test
    public void shouldNotBeATerraWdioFunction() {
        PsiElement psiElement = mock(PsiElement.class);
        PsiElement parent = mock(PsiElement.class);
        when(psiElement.getParent()).thenReturn(parent);
        when(parent.getText()).thenReturn("browser.pause");

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isFalse();
    }

    @Test
    public void shouldNotBeATerraWdioFunctionIfThereIsNoParent() {
        PsiElement psiElement = mock(PsiElement.class);

        assertThat(TerraWdioPsiUtil.isAnyOfTerraWdioFunctions(psiElement)).isFalse();
    }

    //hasText

    @Test
    public void shouldHaveText() {
        JSExpressionStatement psiElement = mockExpressionStatementFor("Terra.validates.screenshot");

        assertThat(TerraWdioPsiUtil.hasText(psiElement, "Terra.validates.element", "Terra.validates.screenshot")).isTrue();
    }

    @Test
    public void shouldNotHaveTextWhenThereNoDesiredTextIsMatched() {
        JSExpressionStatement psiElement = mockExpressionStatementFor("browser.pause");

        assertThat(TerraWdioPsiUtil.hasText(psiElement, "Terra.validates.element", "Terra.validates.screenshot")).isFalse();
    }

    @Test
    public void shouldNotHaveTextWhenNoDesiredTextIsSpecified() {
        PsiElement psiElement = mock(PsiElement.class);

        assertThat(TerraWdioPsiUtil.hasText(psiElement)).isFalse();
    }

    @Test
    public void shouldNotHaveTextIfThereIsNoUnderlyingMethodExpression() {
        //Mocks TerraWdioUtil#getMethodExpressionOf
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        when(psiElement.getExpression()).thenReturn(null);

        assertThat(TerraWdioPsiUtil.hasText(psiElement, "Terra.validates.screenshot")).isFalse();
    }

    //isScreenshotValidationCall

    @Test
    public void shouldBeScreenshotValidationCall() {
        JSExpression methodExpression = mock(JSExpression.class);
        when(methodExpression.getText()).thenReturn("Terra.validates.screenshot");

        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(methodExpression)).isTrue();
    }

    @Test
    public void shouldNotBeScreenshotValidationCallForNullExpression() {
        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(null)).isFalse();
    }

    @Test
    public void shouldNotBeScreenshotValidationCall() {
        JSExpression methodExpression = mock(JSExpression.class);
        when(methodExpression.getText()).thenReturn("Terra.validates.accessibility");

        assertThat(TerraWdioPsiUtil.isScreenshotValidationCall(methodExpression)).isFalse();
    }

    private JSExpressionStatement mockExpressionStatementFor(String methodExpressionText) {
        //Mocks TerraWdioUtil#getMethodExpressionOf
        JSExpressionStatement psiElement = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(psiElement.getExpression()).thenReturn(jsCallExpression);
        JSExpression methodExpression = mock(JSExpression.class);
        when(jsCallExpression.getMethodExpression()).thenReturn(methodExpression);
        when(methodExpression.getText()).thenReturn(methodExpressionText);
        return psiElement;
    }
}
