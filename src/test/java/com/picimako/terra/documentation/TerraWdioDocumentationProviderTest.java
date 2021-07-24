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
