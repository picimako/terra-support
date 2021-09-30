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
        JSLiteralExpression jsLiteralExpression = mock(JSLiteralExpression.class);
        when(jsLiteralExpression.isStringLiteral()).thenReturn(true);

        assertThat(JSLiteralExpressionUtil.isJSStringLiteral(jsLiteralExpression)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenElementIsNotLiteralExpression() {
        JSFile notJSLiteralExpression = mock(JSFile.class);

        assertThat(JSLiteralExpressionUtil.isJSStringLiteral(notJSLiteralExpression)).isFalse();
    }
    
    //getStringValue

    @Test
    public void shouldReturnStringValue() {
        JSLiteralExpression jsLiteralExpression = mock(JSLiteralExpression.class);
        when(jsLiteralExpression.getStringValue()).thenReturn("js literal");

        assertThat(JSLiteralExpressionUtil.getStringValue(jsLiteralExpression)).isEqualTo("js literal");
    }

    @Test
    public void shouldReturnNullForNonJSLiteral() {
        JSFile notJSLiteralExpression = mock(JSFile.class);

        assertThat(JSLiteralExpressionUtil.getStringValue(notJSLiteralExpression)).isNull();
    }
}
