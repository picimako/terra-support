//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.js;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import org.junit.Test;

/**
 * Unit test for {@link JSLiteralExpressionUtil}.
 */
public class JSLiteralExpressionUtilTest {

    //isJSStringLiteral
    
    @Test
    public void shouldReturnTrueWhenElementIsStringLiteral() {
        var jsLiteralExpression = mock(JSLiteralExpression.class);
        when(jsLiteralExpression.isStringLiteral()).thenReturn(true);

        assertThat(JSLiteralExpressionUtil.isJSStringLiteral(jsLiteralExpression)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenElementIsNotLiteralExpression() {
        var notJSLiteralExpression = mock(JSFile.class);

        assertThat(JSLiteralExpressionUtil.isJSStringLiteral(notJSLiteralExpression)).isFalse();
    }
    
    //getStringValue

    @Test
    public void shouldReturnStringValue() {
        var jsLiteralExpression = mock(JSLiteralExpression.class);
        when(jsLiteralExpression.getStringValue()).thenReturn("js literal");

        assertThat(JSLiteralExpressionUtil.getStringValue(jsLiteralExpression)).isEqualTo("js literal");
    }

    @Test
    public void shouldReturnNullForNonJSLiteral() {
        var notJSLiteralExpression = mock(JSFile.class);

        assertThat(JSLiteralExpressionUtil.getStringValue(notJSLiteralExpression)).isNull();
    }
}
