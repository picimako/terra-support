//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.terra.psi.js;

import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITHOUT_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITHOUT_IMPORT_FROM_CLAUSE;
import static com.picimako.terra.JavaScriptTestFileSupport.FILE_WITH_IMPORT;
import static com.picimako.terra.JavaScriptTestFileSupport.createJavaScriptFileFromText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * Unit test for {@link ES6ImportUtil}.
 */
public class ES6ImportUtilFindImportTest extends BasePlatformTestCase {

    public void testFoundImportByBaseName() {
        PsiElement psiElement = mockPsiElement(FILE_WITH_IMPORT, "ResponsiveElement");

        ES6ImportDeclaration foundImportDeclaration = ES6ImportUtil.findImportByBaseName(psiElement);
        assertThat(foundImportDeclaration).isNotNull();
        assertThat(foundImportDeclaration.getImportedBindings()).hasSize(1);
        assertThat(foundImportDeclaration.getImportedBindings()[0].getName()).isEqualTo("ResponsiveElement");
    }

    public void testDidNotFindImportByBaseName() {
        PsiElement psiElement = mockPsiElement(FILE_WITH_IMPORT, "Grid");

        assertThat(ES6ImportUtil.findImportByBaseName(psiElement)).isNull();
    }

    public void testReturnImportPath() {
        PsiElement psiElement = mockPsiElement(FILE_WITH_IMPORT, "ResponsiveElement");

        assertThat(ES6ImportUtil.importPathForBaseComponent(psiElement)).isEqualTo("terra-responsive-element");
    }

    public void testReturnNullForNoImportForComponentName() {
        PsiElement psiElement = mockPsiElement(FILE_WITHOUT_IMPORT, "ResponsiveElement");

        assertThat(ES6ImportUtil.importPathForBaseComponent(psiElement)).isNull();
    }

    public void testReturnNullForNoFromClauseForImport() {
        PsiElement psiElement = mockPsiElement(FILE_WITHOUT_IMPORT_FROM_CLAUSE, "ResponsiveElement");

        assertThat(ES6ImportUtil.importPathForBaseComponent(psiElement)).isNull();
    }

    @NotNull
    private PsiElement mockPsiElement(String fileWithImport, String responsiveElement) {
        PsiElement psiElement = mock(PsiElement.class);
        when(psiElement.getContainingFile()).thenReturn(createJavaScriptFileFromText(getProject(), fileWithImport));
        when(psiElement.getText()).thenReturn(responsiveElement);
        return psiElement;
    }
}
