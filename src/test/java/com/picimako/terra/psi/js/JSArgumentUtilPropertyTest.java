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

import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Unit test for {@link JSArgumentUtil}.
 */
public class JSArgumentUtilPropertyTest extends BasePlatformTestCase {

    //getNumericValueOf

    public void testNoValueReturnedForNonLiteralExpression() {
        myFixture.configureByText("Terra-spec.js",
            "describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        Terra.validates.screenshot('test id', { <caret>selector: { } });\n" +
                "    });\n" +
                "});");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isNull();
    }

    public void testNoValueReturnedForNonNumericLiteral() {
        myFixture.configureByText("Terra-spec.js",
            "describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        Terra.validates.screenshot('test id', { <caret>selector: \"#selector\" });\n" +
                "    });\n" +
                "});");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isNull();
    }

    public void testValueReturnedForNumericLiteral() {
        myFixture.configureByText("Terra-spec.js",
            "describe('Terra screenshot', () => {\n" +
                "    it('Test case', () => {\n" +
                "        Terra.validates.screenshot('test id', { <caret>mismatchTolerance: 0.5 });\n" +
                "    });\n" +
                "});");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isEqualTo(0.5);
    }
}
