//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;

import com.picimako.terra.TerraToolkitTestCase;

/**
 * Unit test for {@link TerraDescribeViewportsDocumentationProvider}.
 */
public class TerraDescribeViewportsDocumentationProviderTest extends TerraToolkitTestCase {

    private static final String DOCUMENTATION_HTML = "<div class='definition'><pre>huge</pre></div>"
        + "<div class='content'>"
        + "<table class='sections'>"
        + "<tr><td valign='top' class='section'><p>Minimum value: </td><td valign='top'>1216px</td><td valign='top'></td>"
        + "<tr><td valign='top' class='section'><p>Media Query: </td><td valign='top'><code>@media screen and (min-width: 1216px)</code></td>"
        + "<tr><td valign='top' class='section'><p>Description: </td><td valign='top'>Active from viewport width 1216px and up</td></table></div>";

    private final DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

    public void testShouldReturnDocumentationForViewport() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', ['hug<caret>e'], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isEqualTo(DOCUMENTATION_HTML);
    }

    public void testShouldReturnDocumentationForViewportInDescribeTests() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeTests('viewports', { formFactors: ['hug<caret>e'] }, () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isEqualTo(DOCUMENTATION_HTML);
    }

    public void testShouldReturnNullWhenInputElementIsNotStringLiteral() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', ['hug<caret>e'], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }

    public void testShouldReturnNullWhenInputElementIsNotStringLiteralInDescribeTests() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeTests('viewports', { formFactors: ['hug<caret>e'] }, () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }

    public void testShouldReturnNullWhenInputElementIsLiteralButNotString() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', [2<caret>0], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }

    public void testShouldReturnNullWhenInputElementIsLiteralButNotStringInDescribeTests() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeTests('viewports', { formFactors: [2<caret>0] }, () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }

    public void testShouldReturnNullWhenTheStringLiteralIsNotInTerraDescribeViewportsOrDescribeTests() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            """
                Terra.describeViewports('viewports', ['huge'], () => {
                    describe('terra screenshot', () => {
                       Terra.validates.element('coll<caret>ect');
                    });\
                });""");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }


    public void testShouldReturnNullWhenTheViewportIsNotSupportedByTerra() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeViewports('viewports', ['ginor<caret>mous'], () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }

    public void testShouldReturnNullWhenTheViewportIsNotSupportedByTerraInDescribeTests() {
        myFixture.configureByText("WdioDocumentation-spec.js",
            "Terra.describeTests('viewports', { formFactors: ['ginor<caret>mous'] }, () => {\n" +
                "});");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        assertThat(documentationProvider.generateDoc(element, null)).isNull();
    }
}
