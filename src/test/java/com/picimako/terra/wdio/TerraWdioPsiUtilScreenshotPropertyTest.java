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
