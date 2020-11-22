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
import static org.mockito.Mockito.when;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for {@link TerraDescribeViewportsDocumentationProvider}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TerraDescribeViewportsDocumentationProviderTest {

    private static final String DOCUMENTATION_HTML = "<div class='definition'><pre>huge</pre></div>"
            + "<div class='content'>"
            + "<table class='sections'>"
            + "<tr><td valign='top' class='section'><p>Minimum value: </td><td valign='top'>1216px</td><td valign='top'></td>"
            + "<tr><td valign='top' class='section'><p>Media Query: </td><td valign='top'><code>@media screen and (min-width: 1216px)</code></td>"
            + "<tr><td valign='top' class='section'><p>Description: </td><td valign='top'>Active from viewport width 1216px and up</td></table></div>";
    @Mock
    private JSLiteralExpression jsLiteralExpression;
    @Mock
    private JSArrayLiteralExpression jsArrayLiteralExpression;
    @Mock
    private JSArgumentList jsArgumentList;
    @Mock
    private JSReferenceExpression jsReferenceExpression;

    @Test
    public void shouldReturnDocumentationForViewport() {
        mockCommonPsi();
        when(jsReferenceExpression.getCanonicalText()).thenReturn("Terra.describeViewports");
        when(jsLiteralExpression.getStringValue()).thenReturn("huge");

        DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

        assertThat(documentationProvider.generateDoc(jsLiteralExpression, null)).isEqualTo(DOCUMENTATION_HTML);
    }

    @Test
    public void shouldReturnNullWhenInputElementIsNotStringLiteral() {
        DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

        assertThat(documentationProvider.generateDoc(jsArrayLiteralExpression, null)).isNull();
    }

    @Test
    public void shouldReturnNullWhenInputElementIsLiteralButNotString() {
        when(jsLiteralExpression.isStringLiteral()).thenReturn(false);

        DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

        assertThat(documentationProvider.generateDoc(jsLiteralExpression, null)).isNull();
    }

    @Test
    public void shouldReturnNullWhenTheStringLiteralIsNotInTerraDescribeViewports() {
        mockCommonPsi();
        when(jsReferenceExpression.getCanonicalText()).thenReturn("Terra.validates.element");

        DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

        assertThat(documentationProvider.generateDoc(jsLiteralExpression, null)).isNull();
    }

    @Test
    public void shouldReturnNullWhenTheViewportIsNotSupportedByTerra() {
        mockCommonPsi();
        when(jsReferenceExpression.getCanonicalText()).thenReturn("Terra.describeViewports");
        when(jsLiteralExpression.getStringValue()).thenReturn("notsupportedviewport");

        DocumentationProvider documentationProvider = new TerraDescribeViewportsDocumentationProvider();

        assertThat(documentationProvider.generateDoc(jsLiteralExpression, null)).isNull();
    }

    private void mockCommonPsi() {
        when(jsLiteralExpression.getParent()).thenReturn(jsArrayLiteralExpression);
        when(jsLiteralExpression.isStringLiteral()).thenReturn(true);
        when(jsArrayLiteralExpression.getParent()).thenReturn(jsArgumentList);
        when(jsArgumentList.getPrevSibling()).thenReturn(jsReferenceExpression);
    }
}
