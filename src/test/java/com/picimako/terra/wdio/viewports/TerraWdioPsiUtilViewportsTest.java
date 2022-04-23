//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
