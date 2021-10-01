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

package com.picimako.terra.wdio.viewports;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.wdio.TerraWdioPsiUtil;

/**
 * Unit test for {@link TerraWdioPsiUtil}.
 */
public class TerraWdioPsiUtilViewportsTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/terra/projectroot";
    }

    //getViewportsSet
    
    public void testViewportsSet() {
        myFixture.configureByFile("tests/wdio/duplicateViewportsSet/ViewportsSet-spec.js");
        JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

        assertThat(TerraWdioPsiUtil.getViewports(element)).hasSize(3);
        assertThat(TerraWdioPsiUtil.getViewportsSet(element)).containsExactlyInAnyOrder("small", "huge", "medium");
    }

    public void testEmptyViewportsSet() {
        myFixture.configureByFile("tests/wdio/duplicateViewportsSet/EmptyViewportsSet-spec.js");
        JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

        assertThat(TerraWdioPsiUtil.getViewports(element)).isEmpty();
        assertThat(TerraWdioPsiUtil.getViewportsSet(element)).isEmpty();
    }
    
    //getViewports

    public void testGetsViewports() {
        myFixture.configureByFile("tests/wdio/duplicateViewportsSet/ViewportsSet-spec.js");
        JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

        JSExpression[] viewports = TerraWdioPsiUtil.getViewports(element);
        
        assertThat(TerraWdioPsiUtil.getViewports(viewports)).containsExactly("medium", "huge", "small");
    }
}