//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.wdio;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import com.picimako.terra.psi.js.JSArgumentUtil;

/**
 * Unit test for {@link TerraWdioPsiUtil}.
 */
public class TerraWdioPsiUtilScreenshotPropertyTest extends BasePlatformTestCase {

    // getScreenshotValidationProperty

    public void testPropertyRetrieval() {
        Object[][] testCases = new Object[][]{
            {"describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        <caret>Terra.validates.screenshot('test id');\n" +
                "    });\n" +
                "});", null, null},
            {"describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        <caret>Terra.validates.screenshot('test id', { });\n" +
                "    });\n" +
                "});", null, null},
            {"describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        <caret>Terra.validates.screenshot('test id', { selector: '#selector' });\n" +
                "    });\n" +
                "});", "#selector", null},
            {"describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        <caret>Terra.validates.screenshot('test id', { mismatchTolerance: 0.5 });\n" +
                "    });\n" +
                "});", null, 0.5},
            {"describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        <caret>Terra.validates.screenshot('test id', { selector: '#selector', mismatchTolerance: 0.5 });\n" +
                "    });\n" +
                "});", "#selector", 0.5}
        };

        for (int i = 0; i < testCases.length; i++) {
            myFixture.configureByText("Terra-spec.js", testCases[i][0].toString());
            JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

            JSProperty selector = TerraWdioPsiUtil.getScreenshotValidationProperty(element, "selector");
            if (testCases[i][1] == null) {
                assertThat(selector).isNull();
            } else {
                assertThat(selector).extracting(select -> JSPsiUtil.getStringLiteralValue(select.getValue())).isEqualTo(testCases[i][1]);
            }

            JSProperty mismatchTolerance = TerraWdioPsiUtil.getScreenshotValidationProperty(element, "mismatchTolerance");
            if (testCases[i][2] == null) {
                assertThat(mismatchTolerance).isNull();
            } else {
                assertThat(mismatchTolerance).extracting(JSArgumentUtil::getNumericValueOf).isEqualTo(testCases[i][2]);
            }
        }
    }

    // getTerraValidationProperties


    public void testRetrievesTerraValidationProperties() {
        myFixture.configureByText("Terra-spec.js", "describe('Terra screenshot', () => {\n" +
            "    it('Test case', () => {\n" +
            "        <caret>Terra.validates.screenshot('test id', { selector: '#selector', mismatchTolerance: 0.5 });\n" +
            "    });\n" +
            "});");

        JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

        assertThat(TerraWdioPsiUtil.getTerraValidationProperties(element)).extracting(JSProperty::getName).containsExactly("selector", "mismatchTolerance");
    }

    public void testNoTerraValidationProperties() {
        myFixture.configureByText("Terra-spec.js", "describe('Terra screenshot', () => {\n" +
            "    it('Test case', () => {\n" +
            "        <caret>Terra.validates.screenshot('test id');\n" +
            "    });\n" +
            "});");

        JSExpressionStatement element = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), JSExpressionStatement.class);

        assertThat(TerraWdioPsiUtil.getTerraValidationProperties(element)).isEmpty();
    }
}
