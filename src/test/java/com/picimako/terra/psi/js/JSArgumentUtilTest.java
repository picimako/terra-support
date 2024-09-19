//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.js;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.intellij.lang.ecmascript6.psi.ES6ClassExpression;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit test for {@link JSArgumentUtil}.
 */
public class JSArgumentUtilTest {

    //getArgumentListOf

    @Test
    public void shouldReturnArgumentList() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        JSArgumentList argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentListOf(element)).isSameAs(argumentList);
        }
    }

    @Test
    public void shouldNotReturnArgumentList() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentListOf(element)).isNull();
        }
    }

    //getArgumentsOf

    @Test
    public void shouldReturnArguments() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        JSArgumentList argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(JSExpression.class)});
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentsOf(element)).hasSize(2);
        }
    }

    @Test
    public void shouldReturnEmptyArgumentsWhenThereIsNoCallExpression() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(null);

            assertThat(JSArgumentUtil.getArgumentsOf(element)).isEmpty();
        }
    }

    @Test
    public void shouldReturnEmptyArgumentsWhenThereIsNoArgumentList() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            assertThat(JSArgumentUtil.getArgumentsOf(element)).isEmpty();
        }
    }

    //doWithinArgumentListOf

    @Test
    public void shouldRunLogicOnArgumentList() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        JSArgumentList argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(JSExpression.class)});
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            List<String> aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(element, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).hasSize(1);
        }
    }

    @Test
    public void shouldNotRunLogicOnArgumentListWhenThereIsNoCallExpression() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(null);

            List<String> aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(element, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).isEmpty();
        }
    }

    @Test
    public void shouldNotRunLogicOnArgumentListWhenThereIsNoArgumentList() {
        JSExpressionStatement element = mock(JSExpressionStatement.class);
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);
        try (MockedStatic<JSPsiUtil> util = Mockito.mockStatic(JSPsiUtil.class)) {
            util.when(() -> JSPsiUtil.getCallExpression(element)).thenReturn(jsCallExpression);

            List<String> aList = new ArrayList<>();
            JSArgumentUtil.doWithinArgumentListOf(element, jsArgumentList -> aList.add("Executed logic."));
            assertThat(aList).isEmpty();
        }
    }

    //getNthArgumentOfMoreThanOne

    @Test
    public void shouldGetNthArgument() {
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        JSArgumentList argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class), mock(ES6ClassExpression.class)});

        JSExpression nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isInstanceOf(ES6ClassExpression.class);
    }

    @Test
    public void shouldNotGetNthArgumentForNullJsCallExpression() {
        assertThat(JSArgumentUtil.getNthArgumentOfMoreThanOne(null, 2)).isNull();
    }

    @Test
    public void shouldNotGetNthArgumentForNullArgumentList() {
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        when(jsCallExpression.getArgumentList()).thenReturn(null);

        JSExpression nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isNull();
    }

    @Test
    public void shouldNotGetNthArgumentForLessThanTwoArguments() {
        JSCallExpression jsCallExpression = mock(JSCallExpression.class);
        JSArgumentList argumentList = mock(JSArgumentList.class);
        when(jsCallExpression.getArgumentList()).thenReturn(argumentList);
        when(argumentList.getArguments()).thenReturn(new JSExpression[]{mock(JSExpression.class)});

        JSExpression nthArgument = JSArgumentUtil.getNthArgumentOfMoreThanOne(jsCallExpression, 2);

        assertThat(nthArgument).isNull();
    }
}
