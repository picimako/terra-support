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

import java.util.ArrayList;
import java.util.List;

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
}
