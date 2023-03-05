//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiElement;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraWdioDocumentationProvider}.
 */
public class TerraWdioDocumentationProviderTest extends TerraToolkitTestCase {

    private final TerraWdioDocumentationProvider provider = new TerraWdioDocumentationProvider();

    public void testReturnNoDocumentationWhenNoOriginalElement() {
        assertThat(provider.generateDoc(null, null)).isNull();
    }

    public void testReturnNoDocumentationWhenContainingFileIsNotASpecFile() {
        myFixture.configureByText("WdioDocumentation.js",
            "describe('terra screenshot', () => {\n" +
                "    Terra.validates.<caret>element('collect');\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(provider.generateDoc(null, element)).isNull();
    }

    public void testReturnNoDocumentationWhenOriginalElementIsNotTerraWdio() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "descri<caret>be('terra screenshot', () => {\n" +
                "    Terra.validates.element('collect');\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(provider.generateDoc(null, element)).isNull();
    }

    public void testGeneratesDocumentation() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "describe('terra screenshot', () => {\n" +
                "    Terra.validates.access<caret>ibility('collect');\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(provider.generateDoc(element, element)).isEqualTo("null<div class='content'><a href=\"https://github.com/cerner/terra-toolkit-boneyard/blob/main/docs/Wdio_Utility.md#test-assertion-helpers\">Webdriver.io Utility Developer's Guide / Test Assertion Helpers</a><br><a href=\"https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md\">Axe Accessibility Rule Descriptions</a></div>");
    }
}
