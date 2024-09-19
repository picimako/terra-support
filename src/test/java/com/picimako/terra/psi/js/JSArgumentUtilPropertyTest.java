//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
            """
                describe('Terra screenshot', () => {
                    it('Test case', () => {
                        Terra.validates.screenshot('test id', { <caret>selector: { } });
                    });
                });""");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isNull();
    }

    public void testNoValueReturnedForNonNumericLiteral() {
        myFixture.configureByText("Terra-spec.js",
            """
                describe('Terra screenshot', () => {
                    it('Test case', () => {
                        Terra.validates.screenshot('test id', { <caret>selector: "#selector" });
                    });
                });""");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isNull();
    }

    public void testValueReturnedForNumericLiteral() {
        myFixture.configureByText("Terra-spec.js",
            """
                describe('Terra screenshot', () => {
                    it('Test case', () => {
                        Terra.validates.screenshot('test id', { <caret>mismatchTolerance: 0.5 });
                    });
                });""");

        JSProperty property = (JSProperty) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertThat(JSArgumentUtil.getNumericValueOf(property)).isEqualTo(0.5);
    }
}
