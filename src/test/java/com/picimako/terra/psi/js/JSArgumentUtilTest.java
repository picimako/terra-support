//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.js;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import com.intellij.lang.ecmascript6.psi.ES6ClassExpression;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link JSArgumentUtil}.
 */
public class JSArgumentUtilTest {

    //getArgumentListOf

    @Test
    public void shouldReturnArgumentList() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        var argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentListOf(expressionStatement)).isSameAs(argumentList);
        }
    }

    @Test
    public void shouldNotReturnArgumentList() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentListOf(expressionStatement)).isNull();
        }
    }

    //getArgumentsOf

    @Test
    public void shouldReturnArguments() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        var argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(JSExpression.class)});
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentsOf(expressionStatement)).hasSize(2);
        }
    }

    @Test
    public void shouldReturnEmptyArgumentsWhenThereIsNoCallExpression() {
        var expressionStatement = mock(JSExpressionStatement.class);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(null);

            assertThat(JSArgumentUtil.getArgumentsOf(expressionStatement)).isEmpty();
        }
    }

    @Test
    public void shouldReturnEmptyArgumentsWhenThereIsNoArgumentList() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentsOf(expressionStatement)).isEmpty();
        }
    }

    //doWithinArgumentListOf

    @Test
    public void shouldRunLogicOnArgumentList() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        var argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(JSExpression.class)});
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            var aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(expressionStatement, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).hasSize(1);
        }
    }

    @Test
    public void shouldNotRunLogicOnArgumentListWhenThereIsNoCallExpression() {
        var expressionStatement = mock(JSExpressionStatement.class);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(null);

            var aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(expressionStatement, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).isEmpty();
        }
    }

    @Test
    public void shouldNotRunLogicOnArgumentListWhenThereIsNoArgumentList() {
        var expressionStatement = mock(JSExpressionStatement.class);
        var jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);
        try (var util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(expressionStatement)).thenReturn(jsCallExpression);

            var aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(expressionStatement, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).isEmpty();
        }
    }

    //getNthArgumentOfMoreThanOne

    @Test
    public void shouldGetNthArgument() {
        var jsCallExpression = mock(JSCallExpression.class);
        var argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(ES6ClassExpression.class)});

        var nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isInstanceOf(ES6ClassExpression.class);
    }

    @Test
    public void shouldNotGetNthArgumentForNullJsCallExpression() {
        assertThat(JSArgumentUtil.getNthArgumentOfMoreThanOne(null, 2)).isNull();
    }

    @Test
    public void shouldNotGetNthArgumentForNullArgumentList() {
        var jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);

        var nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isNull();
    }

    @Test
    public void shouldNotGetNthArgumentForLessThanTwoArguments() {
        var jsCallExpression = mock(JSCallExpression.class);
        var argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class)});

        var nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isNull();
    }
}
